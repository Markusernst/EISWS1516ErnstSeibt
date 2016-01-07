function getAllParking(app, db) {
	return function(req, res){
		db.keys("parking:*", function(err, rep) {
			var parkingList = [];
			if(rep.length == 0) {
				res.json(parkingList);
				return;
			}
			console.log(rep);
			db.mget(rep, function(err, rep) {
				rep.forEach(function(val){
					parkingList.push(JSON.parse(val));
				});
				parkingList = parkingList.map(function(parking) {
					return {
						id: parking.id,
						addresse: parking.addresse,
						typ: parking.typ,
						kapazitaet: parking.kapazitaet,
						belegung: parking.belegung,
						geometry: parking.geometry,
						tendenz: parking.tendenz,
						anbieter: parking.anbieter
					};
				});

				var currentReply = parkingList;
				var reply = {
    			parking: currentReply
				};



				res.json(reply);
			});
		});
	}
}
module.exports = getAllParking;