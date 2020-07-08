import React, { Component } from "react";
import 'bootstrap/dist/css/bootstrap.min.css';
import { Button, Container } from 'react-bootstrap';
import Table from "../utils/Table";

class TableAthletes extends Component {
    constructor(props) {
        super(props);
        this.state = {
        	headers : [
	        	'Name', 'Fecha de Nacimiento', 'Genero', 
	        	'Categoria', 'Licencia', 'Dorsal'
        	],
        	filters : [
	        	{ field : "name", type : "text", name : "Nombre"}, { field : "birthDate", type : "text", name : "Fecha de Nacimiento"}, 
	        	{ field : "gender", type : "text", name : "Genero"}, { field : "category", type : "text", name : "Categoria"}, 
	        	{ field : "license", type : "text", name : "Licencia"}, { field : "dorsalNumber", type : "number", name : "Dorsal"}
        	],
        	entityName : 'athletes',
        }
    }

	render() {
		return (
			<Container>
				<Table 
					headers={this.state.headers} 
					filters={this.state.filters}
					entityName={this.state.entityName} >
				</Table>
				<Button href="/athletes">Crear</Button>
			</Container>
		)
	}
}

export default TableAthletes;


