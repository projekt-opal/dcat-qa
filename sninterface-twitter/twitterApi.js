const util = require('util');
const request = require('request');

const post = util.promisify(request.post);

/*----------------------
Twitter API functions
----------------------*/
/**
 * Marks the message with the specified id from the sender with the specified id as read. 
 * @param {number | string} messageId 
 * @param {number | string} senderId 
 * @param {*} auth 
 */
async function markAsRead(messageId, senderId, auth) {
  const requestConfig = {
    url: 'https://api.twitter.com/1.1/direct_messages/mark_read.json',
    form: {
      last_read_event_id: messageId,
      recipient_id: senderId,
    },
    oauth: auth,
  };

  await post(requestConfig);
}

/**
 * Indicates typing to the user with the specified senderId.
 * @param {number | string} senderId 
 * @param {*} auth 
 */
async function indicateTyping(senderId, auth) {
  const requestConfig = {
    url: 'https://api.twitter.com/1.1/direct_messages/indicate_typing.json',
    form: {
      recipient_id: senderId,
    },
    oauth: auth,
  };

  await post(requestConfig);
}

/**
 * Sends a direct message with the specified text to the user with the specified id.
 * @param {number | string} recipientID 
 * @param {string} message 
 */
async function sendDM(recipientID, message, auth) {
  const requestConfig = {
    url: 'https://api.twitter.com/1.1/direct_messages/events/new.json',
    oauth: auth,
    json: {
      event: {
        type: 'message_create',
        message_create: {
          target: {
            recipient_id: recipientID,
          },
          message_data: {
            text: message,
          },
        },
      },
    },
  };
  await post(requestConfig).then((res) =>{
    console.error(JSON.stringify(res.body));
  });
}

/**
 * Posts a tweet as reply to the tweet with the specified statusID.
 * @param {number | string} statusID 
 * @param {string} reply 
 */
async function tweetReply(statusID, reply, auth) {
  const requestConfig = {
    url: 'https://api.twitter.com/1.1/statuses/update.json',
    oauth: auth,
    form: {
      status: reply,
      in_reply_to_status_id: statusID,
      auto_populate_reply_metadata: true,
    }
  };
  await post(requestConfig).then((res) => {
    console.log(JSON.stringify(res, null, 2))
  });
}

module.exports = { markAsRead, indicateTyping, sendDM, tweetReply }
