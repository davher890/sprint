import React, { Component } from "react";
import "./User.css";
import { Form, Button } from 'react-bootstrap';
import Container from 'react-bootstrap/Container';
import Row from 'react-bootstrap/Row';
import 'bootstrap/dist/css/bootstrap.min.css';

class CreateRunner extends Component {
    constructor(props) {
        super(props);
        this.state = {
        	id : 0,
            name : "",
            email : ""
        };
        this.handleInputChange = this.handleInputChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    componentDidMount(){
        if (this.props.match.params) {
            let id = this.props.match.params.id
            this.setState({ id : id})

            const headers = { 'Content-Type': 'application/json' }
            fetch("http://localhost:9000/users/" + id,  { headers })
                .then(res => res.json())
                .then(data => this.setState(data[0]));
        }
    }

    componentDidUpdate(prevProps) {
        if (prevProps.id !== this.props.id) {
            console.log(prevProps.id, this.props.id)
        }
    }

    handleSubmit(event) {

        event.preventDefault();

        const requestOptions = {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(this.state)
        }

        fetch('http://localhost:9000/users', requestOptions)
            .then(response => console.log(response))
            .then(data => this.setState(data));
    }

    handleInputChange(event) {
        const target = event.target;
        const value = target.value;
        const name = target.name;

        this.setState({
            [name] : value
        });
    }

    render() {
        return (
            <Form onSubmit={this.handleSubmit}>
                <Container>
                    <Row>
                        <Form.Group>
                            <Form.Control name="name" type="text" placeholder="Name" value={this.state.name} onChange={this.handleInputChange}/>
                        </Form.Group>
                    </Row>
                    <Row>
                        <Form.Group>
                            <Form.Control name="email" type="email" placeholder="Email" value={this.state.email} onChange={this.handleInputChange} />
                        </Form.Group>
                    </Row>
                    <Row>
                        <Button type="submit">Submit</Button>
                    </Row>
                </Container>
            </Form>
        );
    }
}

export default CreateUser;
