import React, { Component } from "react";
import 'bootstrap/dist/css/bootstrap.min.css';
import { Container } from 'react-bootstrap';
import Table from "../utils/Table";
import { textFilter } from 'react-bootstrap-table2-filter';

class TableSportSchools extends Component {
    constructor(props) {
        super(props);
        this.state = { 
        	columns : [
        		{ dataField: 'name', text: 'Nombre', filter: textFilter() },
        		{ dataField: 'municipality', text: 'Municipio', filter: textFilter() },
        		{ dataField: 'address', text: 'Dirección', filter: textFilter() }
    		],
    		entityName : 'sport_schools'
        };
    }

    dataConversor(d) {
    	return {
			id : d.id,
			name : d.name,
			municipality : d.municipality,
			address : d.address
		}
    }

    render() {
		return (
			<Container>
				<Table 
					columns={this.state.columns} 
					entityName={this.state.entityName}
					dataConversor={this.dataConversor}>
				</Table>
			</Container>
		)
	}
}

export default TableSportSchools;


