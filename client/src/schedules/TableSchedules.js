import React, { Component } from "react";
import 'bootstrap/dist/css/bootstrap.min.css';
import { Button, Container } from 'react-bootstrap';
import Table from "../utils/Table";


class TableSchedules extends Component {
    constructor(props) {
        super(props);
        this.state = {
        	items : [],
        	headers : ['Id', 'Día', 'Inicio', 'Fin'],
        	entityName : 'schedules'
        }
    }

    componentDidMount() {

		const headers = { 'Content-Type': 'application/json' }
		fetch(process.env.REACT_APP_SERVER_URL + "/" + this.state.entityName,  { headers })
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
					items: items
				})
			});
    }

	render() {
		return (
			<Container>
				<Table items={this.state.items} headers={this.state.headers} entityName={this.state.entityName}></Table>
				<Button href="/schedules">Crear</Button>
			</Container>
		)
	}
}

export default TableSchedules;


