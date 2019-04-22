const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();


exports.notifyOnNewComment = functions.database.ref('/comments/{commentId}')
    .onCreate( (snapshot) => {

        let commentData = snapshot.val();
        console.log(commentData);

        let message = {
            topic: commentData.cardId,
            data: {
                card_key: commentData.cardId,
                comment_key: commentData.key,
                text: commentData.text,
                user_name: commentData.userName
            }
        }
        console.log(message);

        admin.messaging().send(message)
            .then((response) => {
                return true;
            })
            .catch((error) => {
                console.log('Error sending new comment notification:', error);
                return false;
            })

        return true;
    });