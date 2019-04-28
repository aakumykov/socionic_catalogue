const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();


exports.notifyOnNewComment = functions.database.ref("/comments/{commentId}")
    .onCreate( (snapshot) => {

        let commentData = snapshot.val();
        //console.log(commentData);

        let message = {
            topic: commentData.cardId,
            data: {
            	notification_type: "new_comment",
                text: commentData.text,

                comment_id: commentData.key,
                comment_user_id: commentData.userId,
                comment_user_name: commentData.userName,

                card_id: commentData.cardId,
            }
        }
        //console.log(message);

        admin.messaging().send(message)
            .then((response) => {
                return true;
            })
            .catch((error) => {
                console.log("Error sending new comment notification:", error);
                return false;
            })

        return true;
    });