import React, { Component } from "react";
import 'bootstrap/dist/css/bootstrap.min.css';
import { Button } from 'react-bootstrap';

import { Link } from "react-router-dom";

class TableSchedules extends Component {
    constructor(props) {
        super(props);
        this.state = {
        	items : []
        }

        this.renderTableHeader = this.renderTableHeader.bind(this);
		this.renderTableData = this.renderTableData.bind(this);
    }

    componentDidMount() {

		const headers = { 'Content-Type': 'application/json' }
		fetch("http://localhost:9000/schedules",  { headers })
			.then(res => res.json())
			.then(data => this.setState({items: data}));
    }

    renderTableData() {
    	if (this.state.items.length > 0){
			return this.state.items.map((entity, index) => {
				const { id, day, startHour, startMinute, endHour, endMinute } = entity //destructuring
				return (
					<tr key={id}>
					<td>{id}</td>
					<td>{day}</td>
					<td>{startHour}</td>
					<td>{endHour}</td>
					<td>{startMinute}</td>
					<td>{endMinute}</td>
					<td><Link to={`/schedule/${id}`}>
							<Button>Edit</Button>
						</Link></td>
					</tr>
				)
			})
		}
	}

	renderTableHeader() {
		if (this.state.items.length > 0){
			let header = Object.keys(this.state.items[0])
			return header.map((key, index) => {
				return <th key={index}>{key.toUpperCase()}</th>
			})
		}
	}

	render() {
		return (
			<table>
				<thead>
					<tr>{this.renderTableHeader()}</tr>
				</thead>
				<tbody>{this.renderTableData()}</tbody>
			</table>
		)
	}
}

export default TableSchedules;


