const express = require('express');
const app = express();

app.use(express.json());

const voznje = [
	{id:1, ime: 'ales', kolicina: 2, cena:50, datum:Date.now()},
	{id:2, ime: 'anze', kolicina: 1, cena:40, datum:Date.now()},
	{id:3, ime: 'janko', kolicina: 3, cena:60, datum:Date.now()},
];

//GET
app.get('/api/statistika', (req, res) =>{
	res.send(voznje);

});

//GET:id
app.get('/api/statistika/:id', (req, res) =>{
	const voznja = voznje[req.params.id-1];
	if (!voznja) {
		res.status(404).send("ID ne obstaja");
		return;
	}

	res.send(voznja);

});

//POST
app.post('/api/statistika', (req, res) =>{

	const voznja = {
		id: voznje.length+1,
		ime: req.body.ime,
		kolicina: req.body.kolicina,
		cena: req.body.cena,
		datum: Date.now()
	};
	voznje.push(voznja);
	res.send(req.query);
});

//PUT
app.put('/api/statistika/:id', (req, res) =>{
	const voznja = voznje[req.params.id-1];
	if (!voznja) {
		res.status(404).send("ID ne obstaja");
		return;
	}
	const v = {
		id: voznje[req.params.id-1].id,
		ime: req.body.ime,
		kolicina: req.body.kolicina,
		cena: req.body.cena,
		datum: Date.now()
	};
	voznje[req.params.id-1] = v;


	res.send(voznje[req.params.id-1]);
});

//DELETE
app.delete('/api/statistika/:id', (req, res) =>{
	const voznja = voznje.find(c => c.id == parseInt(req.params.id));

	if (!voznja) {
		res.status(404).send("ID ne obstaja");
		return;
	}
	const index = voznje.indexOf(voznja);
	voznje.splice(index, 1);

	res.send(voznja);
});

app.listen(3000, () => console.log("Poslusamo na portu 3000..."));