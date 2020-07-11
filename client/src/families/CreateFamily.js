import React, { Component } from "react";
import { 
    Form, InputGroup,
    Button, Container,
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
            .then(res => res.json())
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
            	<Container fluid>
                    <Form.Group>
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
		            </Form.Group>
                    <Form.Group>
                        <Row>
                            <Col>
                                <InputGroup>
                                    <InputGroup.Prepend>
                                        <InputGroup.Text>Mail Padre</InputGroup.Text>
                                    </InputGroup.Prepend>
                                    <Form.Control name="fatherEmail" type="mail" value={this.state.family.fatherEmail} onChange={this.handleInputChange} />
                                </InputGroup>
                            </Col>
                            <Col>
                                <InputGroup>
                                    <InputGroup.Prepend>
                                        <InputGroup.Text>Mail Madre</InputGroup.Text>
                                    </InputGroup.Prepend>
                                    <Form.Control name="motherEmail" type="mail" value={this.state.family.motherEmail} onChange={this.handleInputChange} />
                                </InputGroup>
                            </Col>
                        </Row>
                    </Form.Group>
                    <Form.Group>
                        <Row>
                            <Col>
                                <InputGroup>
                                    <InputGroup.Prepend>
                                        <InputGroup.Text>Teléfono Padre</InputGroup.Text>
                                    </InputGroup.Prepend>
                                    <Form.Control name="fatherPhone" type="tel" value={this.state.family.fatherPhone} onChange={this.handleInputChange} />
                                </InputGroup>
                            </Col>
                            <Col>
                                <InputGroup>
                                    <InputGroup.Prepend>
                                        <InputGroup.Text>Teléfono Madre</InputGroup.Text>
                                    </InputGroup.Prepend>
                                    <Form.Control name="motherPhone" type="tel" value={this.state.family.motherPhone} onChange={this.handleInputChange} />
                                </InputGroup>
                            </Col>
                        </Row>
                    </Form.Group>
                    <Form.Group>
                        <Row>
                            <Col>
                                <InputGroup>
                                    <InputGroup.Prepend>
                                        <InputGroup.Text>Dni Padre</InputGroup.Text>
                                    </InputGroup.Prepend>
                                    <Form.Control name="fatherDni" type="text" value={this.state.family.fatherDni} onChange={this.handleInputChange} />
                                </InputGroup>
                            </Col>
                            <Col>
                                <InputGroup>
                                    <InputGroup.Prepend>
                                        <InputGroup.Text>Dni Madre</InputGroup.Text>
                                    </InputGroup.Prepend>
                                    <Form.Control name="motherDni" type="text" value={this.state.family.motherDni} onChange={this.handleInputChange} />
                                </InputGroup>
                            </Col>
                        </Row>
                    </Form.Group>
                    <Form.Group>
			            <Row>
			                <Button type="submit">Submit</Button>
			            </Row>
		            </Form.Group>
	            </Container>
            </Form>
        );
    }
}

export default CreateGroup;
