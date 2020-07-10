import React, { Component } from "react";
import 'bootstrap/dist/css/bootstrap.min.css';
import { Container } from 'react-bootstrap';
import Table from "../utils/Table";
import { textFilter } from 'react-bootstrap-table2-filter';

class TableAthletes extends Component {
    constructor(props) {
        super(props);
        this.state = {
        	columns : [
	        	{ dataField: 'name', text : 'Nombre', filter: textFilter() }, 
	        	{ dataField: 'birthDate', text : 'Fecha de Nacimiento' }, 
	        	{ dataField: 'gender', text : 'Genero', filter: textFilter() }, 
	        	{ dataField: 'category', text : 'Categoria', filter: textFilter() }, 
	        	{ dataField: 'license', text : 'Licencia' }, 
	        	{ dataField: 'dorsalNumber', text : 'Dorsal'}
        	],
        	entityName : 'athletes',
        }
    }

    dataConversor(d) {
    	return {
			id : d.id,
			name : d.name, 
			birth_date: d.birthDate, 
			gender: d.gender,
			category : d.category,
			license : d.license,
			dorsal : d.dorsalNumber
		}
    }

	render() {
		return (
			<Container>
				<Table 
					columns={this.state.columns} 
					entityName={this.state.entityName}
					dataConversor={this.dataConversor} >
				</Table>
			</Container>
		)
	}
}

export default TableAthletes;


