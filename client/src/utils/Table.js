import React, { Component } from "react";
import 'bootstrap/dist/css/bootstrap.min.css';
import { Button } from 'react-bootstrap';
import { Link } from "react-router-dom";

class Table extends Component {
    constructor(props) {
        super(props);
        this.state = {
        	items : [],
        	headers : [],
        	entityName : ''
        }

        this.renderTableHeader = this.renderTableHeader.bind(this);
		this.renderTableData = this.renderTableData.bind(this);
    }

    renderTableData() {
    	if (this.props.items.length > 0){
			return this.props.items.map((entity, index) => {

				
				console.log(entity)
				return (
					<tr key={entity.id}>
					
					{ Object.keys(entity).map(key => {
							return <td>{entity[key]}</td>
						})
					}

					<td>
						<Link to={`/${this.entityName}/${entity.id}`}>
							<Button>Edit</Button>
						</Link></td>
					</tr>
				)
			})
		}
	}

	renderTableHeader() {
		if (this.props.headers && this.props.headers.length > 0){
			return this.props.headers.map((key, index) => {
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

export default Table;
