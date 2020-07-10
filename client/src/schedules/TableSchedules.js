import React, { Component } from "react";
import 'bootstrap/dist/css/bootstrap.min.css';
import { Container } from 'react-bootstrap';
import Table from "../utils/Table";


class TableSchedules extends Component {
    constructor(props) {
        super(props);
        this.state = {
        	columns : [ 
        		{ dataField: 'day', text : 'Día'} , 
        		{ dataField: 'start', text : 'Inicio' }, 
        		{ dataField: 'end', text : 'Fin' }
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
					columns={this.state.columns} 
					entityName={this.state.entityName}
					dataConversor={this.dataConversor}>
				</Table>
			</Container>
		)
	}
}

export default TableSchedules;


