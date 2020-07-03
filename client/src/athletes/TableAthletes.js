import React, { Component } from "react";
import 'bootstrap/dist/css/bootstrap.min.css';
import { Button } from 'react-bootstrap';

import { Link } from "react-router-dom";

class TableAthletes extends Component {
    constructor(props) {
        super(props);
        this.state = [];

        this.renderTableHeader = this.renderTableHeader.bind(this);
		this.renderTableData = this.renderTableData.bind(this);
    }

    componentDidMount() {

		const headers = { 'Content-Type': 'application/json' }
		fetch("http://localhost:9000/athletes",  { headers })
			.then(res => res.json())
			.then(data => this.setState(data));
    }

    renderTableData() {
    	if (this.state.length > 0){
			return this.state.map((entity, index) => {
				const { id, name, email } = entity //destructuring
				return (
					<tr key={id}>
					<td>{id}</td>
					<td>{name}</td>
					<td>{email}</td>
					<td><Link to={`/entity/${id}`}>
							<Button>Edit</Button>
						</Link></td>
					</tr>
				)
			})
		}
	}

	renderTableHeader() {
		if (this.state.length > 0){
			let header = Object.keys(this.state[0])
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

export default TableAthletes;


