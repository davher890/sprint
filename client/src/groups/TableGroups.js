import React, { Component } from "react";
import 'bootstrap/dist/css/bootstrap.min.css';
import { Button, Container } from 'react-bootstrap';
import Table from "../utils/Table";

class TableGroups extends Component {
    constructor(props) {
        super(props);
        this.state = { 
        	headers : ['Nombre'],
        	filters : [
	        	{ field : "name", type : "text", name : "Nombre"}
        	],
    		entityName : 'groups'
        };
    }

    dataConversor(d) {
    	return {
			name : d.name,
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
					dataConversor={this.dataConversor}>
				</Table>
			</Container>
		)
	}
}

export default TableGroups;


