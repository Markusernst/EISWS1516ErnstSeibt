function register(app, db) {
	app.post("/parking", require("./post-parking")(app, db));
	app.get("/parking/:id", require("./get-parking")(app, db));
	app.put("/parking/:id", require("./change-parking")(app, db));
	app.delete("/parking/:id", require("./delete-parking")(app, db));
	app.get("/parking", require("./get-all-parking")(app, db));
	app.get("/search/:term", require("./get-parking-search")(app, db));
}
module.exports = {
	register: register
};