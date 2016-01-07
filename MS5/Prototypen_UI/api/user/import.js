function register(app, db) {
	app.post("/user", require("./post-user")(app, db));
	app.get("/user/:id", require("./get-user")(app, db));
	app.put("/user/:id", require("./put-user")(app, db));
	app.delete("/user/:id", require("./delete-user")(app, db));
	app.get("/user", require("./get-all-users")(app, db));
}
module.exports = {
	register: register
};