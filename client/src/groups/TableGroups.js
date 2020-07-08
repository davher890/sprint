import React, { Component } from "react";
import 'bootstrap/dist/css/bootstrap.min.css';
import { Button, Container } from 'react-bootstrap';
import Table from "../utils/Table";

class TableGroups extends Component {
    constructor(props) {
        super(props);
        this.state = { 
        	headers : ['Id', 'Nombre'],
    		entityName : 'groups'
        };
    }

    render() {
		return (
			<Container>
				<Table 
					headers={this.state.headers} 
					entityName={this.state.entityName}></Table>
				<Button href={this.state.entityName}>Crear</Button>
			</Container>
		)
	}
}

export default TableGroups;


