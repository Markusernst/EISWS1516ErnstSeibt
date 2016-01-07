function getParkingSearch(app, db) {
	return function(req, res){
		db.keys("parking:*", function(err, rep) {
			var parkingList = [];
			var parkingListResults = [];
			if(rep.length == 0) {
				res.json(parkingList);
				return;
			}

			console.log(req.params.term);

			db.mget(rep, function(err, rep) {
				rep.forEach(function(val){
					parkingList.push(JSON.parse(val));
				});



				parkingList = parkingList.map(function(parking) {
					var parkingaddresse = parking.adresse;
					parkingaddresse = parkingaddresse.replace(/\s/g, '');
					if( parkingaddresse.toLowerCase().indexOf(req.params.term.toLowerCase()) > -1) {
						parkingListResults.push(parking);
					}
				});
				console.log(parkingListResults);


				var currentReply = parkingListResults;
				var reply = {
    			parking: currentReply
				};



				res.json(reply);
			});
		});
	}
}
module.exports = getParkingSearch;