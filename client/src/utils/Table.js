import React, { Component } from "react";
import 'bootstrap/dist/css/bootstrap.min.css';
import { 
	Button, 
	Container, 
	Row, 
	Form, 
	Col 
} from 'react-bootstrap';
import { Link } from "react-router-dom";
import  TableB  from 'react-bootstrap/table';


class Table extends Component {
    constructor(props) {
        super(props);
        this.state = {
        	filters : props.filters,
        	urlParams : '',
        	data : {},
    	}
        this.renderTableHeader = this.renderTableHeader.bind(this);
		this.renderTableData = this.renderTableData.bind(this);
		this.handleInputChange = this.handleInputChange.bind(this);
		this.fetchData = this.fetchData.bind(this);
        this.filterData = this.filterData.bind(this);
    }

    componentDidMount() {
    	this.fetchData('')
    }

    fetchData (urlParams){
		const headers = { 'Content-Type': 'application/json' }
		fetch(process.env.REACT_APP_SERVER_URL + "/" + this.props.entityName + urlParams,  { headers })
			.then(res => res.json())
			.then(data => {

				let items = data.content.map(d => {
					return this.props.dataConversor(d)
				})
				data.content = items
				this.setState({
					data: data
				})
			});
	}

    renderTableData() {
    	if (this.state.data && this.state.data.content && this.state.data.content.length > 0){
			return this.state.data.content.map((entity, index) => {

				return (
					<tr key={entity.id}>
					
						{ 
							Object.keys(entity).filter(x => x != 'id').map(key => {
								return <td key={key}>{entity[key]}</td>
							})
						}

						<td>
							<Link to={`/${this.props.entityName}/${entity.id}`}>
								<Button>Edit</Button>
							</Link>
						</td>
					</tr>
				)
			})
		}
	}

	renderTableHeader() {
		if (this.props.headers && this.props.headers.length > 0){
			return this.props.headers.map((key, index) => {
				return <th key={index}>{key}</th>
			})
		}
	}

	handleInputChange(event){

		event.preventDefault();
		const target = event.target;
        const value = target.value;
        const name = target.name;

        let filters = this.state.filters.map(filter => {
        	if (filter.field === name){
        		filter.value = value
        	}
        	return filter
    	})

        this.setState({
        	filters : filters
        })
        if (this.filterData){
	        this.filterData()
	    }
	}


	filterData(){

		let filterParams = this.state.filters.flatMap(filter => {
			if (filter.value && filter.value.length > 2){
				return filter.field + "__like__" + filter.value
			}
		}).filter(x => x)

		if (filterParams && filterParams.length > 0){
		
			let urlParams = '?filters=' + filterParams.reduce((accumulator, currentValue) => accumulator + ',' + currentValue)
	        this.fetchData(urlParams)
	    }
	    else {
	    	this.fetchData('')
	    }
	}

    render() {
		return (
			<Container>
				<Row>
				{
					this.state.filters.map(filter => {
						return <Col><Form.Control key={filter.field} name={filter.field} type={filter.type} placeholder={filter.name} onChange={this.handleInputChange}/></Col>
					})
				}
				</Row>
				<Row>
					<Col>
						<TableB striped bordered hover>
							<thead>
								<tr>{this.renderTableHeader()}</tr>
							</thead>
							<tbody>{this.renderTableData()}</tbody>
						</TableB>
					</Col>
				</Row>
				<Row>
					<Col>
						<Button href={this.props.entityName}>Crear</Button>
					</Col>
				</Row>
			</Container>
		)
	}
}

export default Table;
