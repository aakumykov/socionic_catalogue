const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();


exports.notifyOnNewCard = functions.database.ref('/cards/{cardId}')
	.onWrite( (change,context) => {

		if (change.after.exists()) {
			let cardData = change.after.val();
			
			var message = {
				topic: 'new_cards',
				data: {
					card_key: cardData.key,
					card_title: cardData.title,
					card_user_id: cardData.userId,
					card_user_name: cardData.userName
				}
			};

			admin.messaging().send(message)
				.then((response) => {
					return true;
				})
				.catch((error) => {
					console.log('Error sending message:', error);
					return false;
				});
		}

		return true;
	});

