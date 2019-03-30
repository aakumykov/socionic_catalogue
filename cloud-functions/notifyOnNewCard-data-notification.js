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
					card_user_id: cardData.userId
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


/*exports.notifyOnNewCardAdvanced = functions.database.ref('/cards/{cardId}').onWrite( (change, context) => {

	if (!change.after.exists()) {
		return null;
	}

	let card_user_id = change.after.val().userId;
	console.log("card_user_id: "+card_user_id);

	let device_id_ref = admin.database().ref("/device_id/");

	device_id_ref.once("value", 
		(snapshot) => {

		let device_id_map = snapshot.val();
		console.log("device_id_map:");
		console.log(device_id_map);

		let target_device_list = {};

		for (device_id in device_id_map) {

			let pieces = device_id_map[device_id].split('__');
			let device_user_id = pieces[0];
			let device_last_time = pieces[1];
			
			console.log("device_user_id: "+device_user_id);
			console.log("device_last_time: "+device_last_time);
			
			if (device_user_id !== card_user_id) {
				target_device_list[target_device_list.length] = device_id;
			}

			console.log("target_device_list:");
			console.log(target_device_list);
		}

		}, 
		(errorObject) => {
			console.log("The read failed: " + errorObject.code);
		}
	);

	return true;
});*/
