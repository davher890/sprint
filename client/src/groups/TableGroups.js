import React, { Component } from "react";
import 'bootstrap/dist/css/bootstrap.min.css';
import { Container } from 'react-bootstrap';
import Table from "../utils/Table";
import { textFilter } from 'react-bootstrap-table2-filter';

class TableGroups extends Component {
    constructor(props) {
        super(props);
        this.state = { 
        	columns : [
        		{ dataField: 'name', text: 'Nombre', filter: textFilter() }
    		],
    		entityName : 'groups'
        };
    }

    dataConversor(d) {
    	return {
			id : d.id,
			name : d.name
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

export default TableGroups;


