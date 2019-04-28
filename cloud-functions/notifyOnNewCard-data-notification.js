const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();


exports.notifyOnNewCard = functions.database.ref("/cards/{cardId}")
	.onCreate( (snapshot,context) => {

		if (snapshot.exists()) {

			let cardData = snapshot.val();
			//console.log(cardData);

			var message = {
				topic: "new_cards",
				data: {
					notification_type: "new_card",
					text: cardData.title,

					card_id: cardData.key,
					card_user_id: cardData.userId,
					card_user_name: cardData.userName,
				}
			};
			//console.log(message);

			admin.messaging().send(message)
				.then((response) => {
					//console.log("Message sended!");
					return true;
				})
				.catch((error) => {
					console.log("Error sending new card notification: ", error);
					return false;
				});
		}

		return true;
	});

