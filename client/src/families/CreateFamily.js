import React, { Component } from "react";
import { 
    Form, InputGroup,
    Button,
    Col, Row
} from 'react-bootstrap';

class CreateGroup extends Component {
    constructor(props) {
        super(props);
        this.state = {
        	family : {}
        };
        this.handleInputChange = this.handleInputChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    componentDidMount(){
        if (this.props.match.params) {
            let id = this.props.match.params.id
            if (id){
                const headers = { 'Content-Type': 'application/json' }
                fetch(process.env.REACT_APP_SERVER_URL + "/families/" + id,  { headers })
                    .then(res => res.json())
                    .then(data => this.setState({ family : data}));
            }
        }
    }

    handleSubmit(event) {

        event.preventDefault();

        const requestOptions = {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(this.state.family)
        }

        fetch(process.env.REACT_APP_SERVER_URL + "/families", requestOptions)
            .then(response => console.log(response))
            .then(data => this.setState(data));
    }

    handleInputChange(event) {

        event.preventDefault();

        const target = event.target;
        const value = target.value;
        const name = target.name;

        let family = this.state.family
        family[name] = value

        this.setState({
            family : family
        });
    }

    render() {
        return (
            <Form onSubmit={this.handleSubmit}>
                <Row>
                    <Col>
                        <InputGroup>
                            <InputGroup.Prepend>
                                <InputGroup.Text>Primer Apellido</InputGroup.Text>
                            </InputGroup.Prepend>
                            <Form.Control key="firstSurname" id='firstSurname' name="firstSurname" type="text" value={this.state.family.firstSurname} onChange={this.handleInputChange}/>
                        </InputGroup>
                    </Col>
                    <Col>
                        <InputGroup>
                            <InputGroup.Prepend>
                                <InputGroup.Text>Segundo Apellido</InputGroup.Text>
                            </InputGroup.Prepend>
                            <Form.Control key="secondSurname" id='secondSurname' name="secondSurname" type="text" value={this.state.family.secondSurname} onChange={this.handleInputChange}/>
                        </InputGroup>
                    </Col>
                </Row>
                <Row>
                    <Button type="submit">Submit</Button>
                </Row>
            </Form>
        );
    }
}

export default CreateGroup;
