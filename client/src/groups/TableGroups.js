import React, { Component } from "react";
import 'bootstrap/dist/css/bootstrap.min.css';
import { Button } from 'react-bootstrap';
import { Link } from "react-router-dom";

class TableGroups extends Component {
    constructor(props) {
        super(props);
        this.state = { 
        	groups : []
        };

        this.renderTableHeader = this.renderTableHeader.bind(this);
		this.renderTableData = this.renderTableData.bind(this);
    }

    componentDidMount() {

		const headers = { 'Content-Type': 'application/json' }
		fetch("http://localhost:9000/groups",  { headers })
			.then(res => res.json())
			.then(data => this.setState({ groups: data }));
    }

    renderTableData() {
    	if (this.state.groups.length > 0){
			return this.state.groups.map((group, index) => {
				const { id, name } = group //destructuring
				return (
					<tr key={id}>
						<td>{id}</td>
						<td>{name}</td>
						<td><Link to={`/group/${id}`}>
								<Button>Edit</Button>
							</Link>
						</td>
					</tr>
				)
			})
		}
	}

	renderTableHeader() {
		if (this.state.groups.length > 0){
			let header = Object.keys(this.state.groups[0])
			return header.map((key, index) => {
				return <th key={index}>{key.toUpperCase()}</th>
			})
		}
	}

	render() {
		return (
			<div>
				<Link to={`/group`}><Button variant="secondary">Crear grupo</Button></Link>
				<table>
					<thead>
						<tr>{this.renderTableHeader()}</tr>
					</thead>
					<tbody>{this.renderTableData()}</tbody>
				</table>
			</div>
		)
	}
}

export default TableGroups;


