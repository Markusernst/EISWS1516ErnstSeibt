function postParking(app, db) {
	return function(req, res) {
		var newParking = req.body;
		db.incr("id:ss", function(err, rep) {
			newParking.id = rep;
			db.set("parking:" + newParking.id, JSON.stringify(newParking), function(err, rep) {
				res.json(newParking);
			});
		});
	};
}
module.exports = postParking;