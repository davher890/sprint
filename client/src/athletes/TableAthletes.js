import React, { Component } from "react";
import 'bootstrap/dist/css/bootstrap.min.css';
import { Button, Container } from 'react-bootstrap';
import Table from "../utils/Table";

class TableAthletes extends Component {
    constructor(props) {
        super(props);
        this.state = {
        	headers : [
	        	'Nombre', 'Fecha de Nacimiento', 'Genero', 
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

    dataConversor(d) {
    	return {
			name : d.name, 
			birth_date: d.birthDate, 
			gender: d.gender,
			category : d.category,
			license : d.license,
			dorsal : d.dorsalNumber,
			id : d.id
		}
    }

	render() {
		return (
			<Container>
				<Table 
					headers={this.state.headers} 
					filters={this.state.filters}
					entityName={this.state.entityName}
					dataConversor={this.dataConversor} >
				</Table>
			</Container>
		)
	}
}

export default TableAthletes;


