const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();


exports.sendMessageToDevice = functions.database.ref('/cards/{cardId}')
	.onWrite( (change,context) => {

		if (change.after.exists()) {
			let cardData = change.after.val();
			
			var registrationToken = 'drMiSgtiHKQ:APA91bFLrbFSzYwjHVtY8zKBQbvilK1TWTaI95v9GjKeRcbDsIvF53NQCNu7M2lhENyLS3UVhLJQNraknxxNET1-IFRTRGyLHZXZXIXX6Gs24NK7YBUXO5US0bVW1A9JCaZMLpSIEnTJ';

			var message = {
				// token: registrationToken,
				notification: {
					title: "Новая карточка",
					body: "«" + cardData.title + "»"
				},
				data: {
					CARD_KEY: cardData.key
				},
				topic: 'new_cards'
			};

			admin.messaging().send(message)
				.then((response) => {
					// Response is a message ID string.
					console.log('Successfully sent message:', response);
					return true;
				})
				.catch((error) => {
					console.log('Error sending message:', error);
					return false;
				});
		}

		return true;
	});
