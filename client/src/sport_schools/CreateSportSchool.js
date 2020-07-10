import React, { Component } from "react";
import { 
    Form, InputGroup,
    Button,
    Col, Row
} from 'react-bootstrap';

class CreateSportSchool extends Component {
    constructor(props) {
        super(props);
        this.state = {
        	sportSchool : {}
        };
        this.handleInputChange = this.handleInputChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    componentDidMount(){
        if (this.props.match.params) {
            let id = this.props.match.params.id
            if (id){
                const headers = { 'Content-Type': 'application/json' }
                fetch(process.env.REACT_APP_SERVER_URL + "/sport_schools/" + id,  { headers })
                    .then(res => res.json())
                    .then(data => this.setState({ sportSchool : data}));


            }
        }
    }

    handleSubmit(event) {

        event.preventDefault();

        const requestOptions = {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(this.state.sportSchool)
        }

        fetch(process.env.REACT_APP_SERVER_URL + "/sport_schools", requestOptions)
            .then(response => console.log(response))
            .then(data => this.setState(data));
    }

    handleInputChange(event) {

        event.preventDefault();

        const target = event.target;
        const value = target.value;
        const name = target.name;

        let sportSchool = this.state.sportSchool
        sportSchool[name] = value

        this.setState({
            sportSchool : sportSchool
        });
    }

    render() {
        return (
            <Form onSubmit={this.handleSubmit}>
                <Row>
                    <Col>
                        <InputGroup>
                            <InputGroup.Prepend>
                                <InputGroup.Text>Nombre</InputGroup.Text>
                            </InputGroup.Prepend>
                            <Form.Control key="name" id='name' name="name" type="text" value={this.state.sportSchool.name} onChange={this.handleInputChange}/>
                        </InputGroup>
                    </Col>
                    <Col>
                        <InputGroup>
                            <InputGroup.Prepend>
                                <InputGroup.Text>Municipio</InputGroup.Text>
                            </InputGroup.Prepend>
                            <Form.Control key="municipality" id='municipality' name="municipality" type="text" value={this.state.sportSchool.municipality} onChange={this.handleInputChange}/>
                        </InputGroup>
                    </Col>
                    <Col>
                        <InputGroup>
                            <InputGroup.Prepend>
                                <InputGroup.Text>Dirección</InputGroup.Text>
                            </InputGroup.Prepend>
                            <Form.Control key="address" id='address' name="address" type="text" value={this.state.sportSchool.address} onChange={this.handleInputChange}/>
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

export default CreateSportSchool;
