import React, { Component } from "react";
import { Form, Button } from 'react-bootstrap';
import Container from 'react-bootstrap/Container';
import Row from 'react-bootstrap/Row';
import 'bootstrap/dist/css/bootstrap.min.css';

class CreateTrainer extends Component {
    constructor(props) {
        super(props);
        this.state = {
            trainer : {},
            groups : []
        };
        this.handleInputChange = this.handleInputChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    componentDidMount(){
        if (this.props.match.params) {
            let id = this.props.match.params.id
            this.setState({ id : id})

            const headers = { 'Content-Type': 'application/json' }
            fetch(process.env.REACT_APP_SERVER_URL + "/trainers/" + id,  { headers })
                .then(res => res.json())
                .then(data => {
                    this.setState({ trainer : data})
                });

            fetch(process.env.REACT_APP_SERVER_URL + "/groups",  { headers })
                .then(res => res.json())
                .then(data => {
                    this.setState({ groups : data.content})
                });
        }
    }

    handleSubmit(event) {

        event.preventDefault();

        const requestOptions = {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(this.state)
        }

        fetch(process.env.REACT_APP_SERVER_URL + "/athletes", requestOptions)
            .then(res => res.json())
            .then(data => this.setState({ trainer : data}));
    }

    handleInputChange(event) {
        const target = event.target;
        const value = target.value;
        const name = target.name;

        let trainer = this.state.trainer
        trainer[name] = value

        this.setState({
            trainer : trainer
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

export default CreateTrainer;
