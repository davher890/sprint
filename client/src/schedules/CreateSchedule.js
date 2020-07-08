import React, { Component } from "react";
import Row from 'react-bootstrap/Row';
import 'bootstrap/dist/css/bootstrap.min.css';

import { 
    Form, 
    Button, 
    Col,
    InputGroup } from 'react-bootstrap';


class CreateSchedule extends Component {

    constructor(props) {
        super(props);
        this.state = {};
        this.handleInputChange = this.handleInputChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    componentDidMount(){
        if (this.props.match.params.id) {
            let id = this.props.match.params.id
            this.setState({ id : id})

            if (id){
                const headers = { 'Content-Type': 'application/json' }
                fetch(process.env.REACT_APP_SERVER_URL + "/schedules/" + id,  { headers })
                    .then(res => res.json())
                    .then(data => this.setState(data[0]));
            }
        }
    }

    formValidation(){
        if (this.state.day && this.state.day !== "" &&
            this.state.startHour >=0 && this.state.startHour <25 &&
            this.state.startMinute >=0 && this.state.endMinute <61){
            return true;
        }
        return false;
    }

    handleSubmit(event) {

        event.preventDefault();

        if (this.formValidation()){

            const requestOptions = {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(this.state)
            }

            fetch(process.env.REACT_APP_SERVER_URL + "/schedules", requestOptions)
                .then(response => console.log(response))
                .then(data => this.setState(data));
        }
        else {
            alert('Formulario Invalido')
        }
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
                <Form className="mx-auto" onSubmit={this.handleSubmit}>
                    <Row>
                        <Col>
                            <Form.Group controlId="day" >
                                <Form.Label>Día de la semana</Form.Label>
                                <Form.Control name="day" as="select" custom onChange={this.handleInputChange}>
                                    <option></option>
                                    <option>Lunes</option>
                                    <option>Martes</option>
                                    <option>Miercoles</option>
                                    <option>Jueves</option>
                                    <option>Viernes</option>
                                    <option>Sabado</option>
                                    <option>Domingo</option>
                                </Form.Control>
                            </Form.Group>
                        </Col>
                        <Col>
                            <InputGroup>
                                <Form.Control as="input" name="startHour" type="number" min="0" max="23" placeholder="Hora Inicio" value={this.state.startHour} onChange={this.handleInputChange}/>
                                <InputGroup.Append>
                                    <InputGroup.Text>:</InputGroup.Text>
                                </InputGroup.Append>
                                <Form.Control as="input" name="startMinute" type="number" min="0" max="59" placeholder="Minuto Inicio" value={this.state.startMinute} onChange={this.handleInputChange} />
                            </InputGroup>
                        </Col>
                        <Col>
                            <InputGroup>
                                <Form.Control as="input" name="endHour" type="number" min="0" max="23" placeholder="Hora Fin" value={this.state.endHour} onChange={this.handleInputChange} />
                                <InputGroup.Append>
                                    <InputGroup.Text id="basic-addon2">:</InputGroup.Text>
                                </InputGroup.Append>
                                <Form.Control as="input" name="endMinute" type="number" min="0" max="59" placeholder="Minuto Fin" value={this.state.endMinute} onChange={this.handleInputChange} />
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

export default CreateSchedule;
