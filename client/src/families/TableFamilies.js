import React, { Component } from "react";
import 'bootstrap/dist/css/bootstrap.min.css';
import { Container } from 'react-bootstrap';
import Table from "../utils/Table";
import { textFilter } from 'react-bootstrap-table2-filter';

class TableFamilies extends Component {
    constructor(props) {
        super(props);
        this.state = {
        	columns : [
	        	{ dataField: 'firstSurname', text : 'Primer Apellido', filter: textFilter() }, 
	        	{ dataField: 'secondSurname', text : 'Segundo Apellido',filter: textFilter() },
	        	{ dataField: 'count', text : 'Número de familiares' }
        	],
        	entityName : 'families',
        }
    }

    dataConversor(d) {
    	return {
			id : d.id,
			firstSurname : d.firstSurname, 
			secondSurname : d.secondSurname,
			count : d.count
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

export default TableFamilies;


