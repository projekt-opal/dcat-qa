package org.dice_research.opal.dcat_qa.launuts;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionRemote;
import org.apache.jena.vocabulary.DCTerms;
import org.dice_research.opal.common.utilities.FileHandler;
import org.dice_research.opal.metadata.GeoData;

public class Main {

	public static void main(String[] args) throws Exception {
		Main main = new Main();

		// Get dataset URIs

		List<String> datasetUris = main.getDatasetUris();
		System.out.println("Number of datasets: " + datasetUris.size());

		// Get place URIs using OPAL metadata refinement

		main.getSpatialUrisDe(datasetUris);
	}

	/**
	 * Gets dataset URIs from cache or SPARQL endpoint.
	 */
	protected List<String> getDatasetUris() throws Exception {
		List<String> datasets = new LinkedList<>();
		File datasetUris = new File(Cfg.getTmpDir(), "datasetUris.txt");

		if (datasetUris.canRead()) {
			System.out.println("Reading " + datasetUris.getAbsolutePath());
			return FileUtils.readLines(datasetUris, "UTF-8");

		} else {
			long time = System.currentTimeMillis();
			try (RDFConnection rdfConnection = RDFConnectionRemote.create().destination(Cfg.getEntpointUri()).build()) {
				String query = IOUtils.toString(this.getClass().getResourceAsStream("select-datasets"), "UTF-8");
				QueryExecution queryExecution = rdfConnection.query(query);
				ResultSet resultSet = queryExecution.execSelect();
				while (resultSet.hasNext()) {
					QuerySolution querySolution = resultSet.next();
					datasets.add(querySolution.get("dataset").toString());
				}
			}
			System.out.println("Writing " + datasetUris.getAbsolutePath());
			FileUtils.writeLines(datasetUris, datasets);
			System.out.println("Runtime: " + (System.currentTimeMillis() - time) / 1000);
			return datasets;
		}
	}

	/**
	 * Calls {@link #getSpatialUrisDe(String)} multiple times.
	 */
	protected Model getSpatialUrisDe(List<String> datasetUris) throws Exception {
		Model model = ModelFactory.createDefaultModel();
		long time = System.currentTimeMillis();
		File spatial = new File(Cfg.getTmpDir(), "spatial.ttl");

		if (spatial.canRead()) {
			System.out.println("Reading " + spatial.getAbsolutePath());
			return FileHandler.importModel(spatial);

		} else {
			for (String datasetUri : datasetUris) {
				for (String spatialUri : getSpatialUrisDe(datasetUri)) {
					model.add(ResourceFactory.createResource(datasetUri), DCTerms.spatial,
							ResourceFactory.createResource(spatialUri));
				}
			}
			System.out.println("Writing " + spatial.getAbsolutePath());
			FileHandler.export(spatial, model);
			System.out.println("Runtime: " + (System.currentTimeMillis() - time) / 1000);
			return model;
		}
	}

	/**
	 * Checks for german titles and descriptions. If found, OPAL-metadata-refinement
	 * is used to search for known locations and get related URIs.
	 */
	protected List<String> getSpatialUrisDe(String datasetUri) throws Exception {
		List<String> placeUris = new LinkedList<>();

		// Get dataset model

		Model model = null;
		try (RDFConnection rdfConnection = RDFConnectionRemote.create().destination(Cfg.getEntpointUri()).build()) {
			String query = IOUtils.toString(this.getClass().getResourceAsStream("construct-title-description"),
					"UTF-8");
			query = query.replace("DATASET", datasetUri);
			QueryExecution queryExecution = rdfConnection.query(query);
			model = queryExecution.execConstruct();
		}
		Resource dataset = ResourceFactory.createResource(datasetUri);

		// Only continue on german title or description

		NodeIterator nodeIterator;
		boolean foundGermanLiteral = false;
		nodeIterator = model.listObjectsOfProperty(dataset, DCTerms.title);
		while (nodeIterator.hasNext()) {
			RDFNode title = nodeIterator.next();
			if (title.isLiteral() && title.asLiteral().getLanguage().startsWith("de")) {
				{
					foundGermanLiteral = true;
				}
			}
		}
		nodeIterator = model.listObjectsOfProperty(dataset, DCTerms.title);
		while (nodeIterator.hasNext()) {
			RDFNode title = nodeIterator.next();
			if (title.isLiteral() && title.asLiteral().getLanguage().startsWith("de")) {
				{
					foundGermanLiteral = true;
				}
			}
		}
		if (!foundGermanLiteral) {
			return placeUris;
		}

		// Add geo information

		GeoData geoGeoData = new GeoData();
		Model newModel = ModelFactory.createDefaultModel().add(model);
		geoGeoData.processModel(newModel, datasetUri);
		if (newModel.size() != model.size()) {
			nodeIterator = newModel.listObjectsOfProperty(dataset, DCTerms.spatial);
			while (nodeIterator.hasNext()) {
				RDFNode node = nodeIterator.next();
				if (node.isURIResource()) {
					placeUris.add(node.asResource().getURI());
				}
			}
		}

		return placeUris;
	}
}