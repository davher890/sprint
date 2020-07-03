import React, { Component } from "react";
import 'bootstrap/dist/css/bootstrap.min.css';
import { Button } from 'react-bootstrap';

import { Link } from "react-router-dom";
import Table from "../utils/Table";


class TableSchedules extends Component {
    constructor(props) {
        super(props);
        this.state = {
        	items : [],
        	headers : []
        }
    }

    componentDidMount() {

		const headers = { 'Content-Type': 'application/json' }
		fetch("http://localhost:9000/schedules",  { headers })
			.then(res => res.json())
			.then(data => {

				let items = data.map(d => {
					return {
						id : d.id, 
						day: d.day, 
						start: d.startHour + ':' + d.startMinute,
						end: d.endHour + ':' + d.endMinute
					}
				})

				this.setState({
					items: items,
					headers: ['Id', 'Día', 'Inicio', 'Fin']
				})
			});
    }

	render() {
		return (
			<Table items={this.state.items} headers={this.state.headers} entityName='schedule'></Table>
		)
	}
}

export default TableSchedules;


