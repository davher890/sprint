import React, { Component } from "react";
import 'bootstrap/dist/css/bootstrap.min.css';
import { Button, Container } from 'react-bootstrap';
import Table from "../utils/Table";


class TableSchedules extends Component {
    constructor(props) {
        super(props);
        this.state = {
        	headers : ['Día', 'Inicio', 'Fin'],
        	filters : [
	        	{ field : "day", type : "text", name : "Día"}
        	],
        	entityName : 'schedules'
        }
    }

    dataConversor(d){
    	return {
			id : d.id, 
			day: d.day, 
			start: d.startHour + ':' + d.startMinute,
			end: d.endHour + ':' + d.endMinute
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

export default TableSchedules;


