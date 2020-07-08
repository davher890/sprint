import React, { Component } from "react";
import 'bootstrap/dist/css/bootstrap.min.css';
import Table from "../utils/Table";

class TableTrainers extends Component {
    constructor(props) {
        super(props);
        this.state = {
        	items : [],
        	headers : ['Id', 'Nombre'],
        	entityName : 'trainers'
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
						name: d.name
					}
				})
				this.setState({
					items: items
				})
			});
    }

	render() {
		return (
			<Table items={this.state.items} headers={this.state.headers} entityName='schedule'></Table>
		)
	}
}

export default TableTrainers;


