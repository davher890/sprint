import React, { Component } from "react";
import { 
    Form, InputGroup,
    Button, Container,
    Col, Row
} from 'react-bootstrap';
import DatePicker from "react-datepicker";
import 'bootstrap/dist/css/bootstrap.min.css';

class CreateAthlete extends Component {
    constructor(props) {
        super(props);
        this.state = {
            athlete : {},
            groups : [],
            families : []
        };
        this.handleInputChange = this.handleInputChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleInputDateChange = this.handleInputDateChange.bind(this);
        this.handleMultipleSelectChange = this.handleMultipleSelectChange.bind(this);
    }

    componentDidMount(){
        if (this.props.match.params.id) {
            let id = this.props.match.params.id
            if (id){
                const headers = { 'Content-Type': 'application/json' }
                fetch(process.env.REACT_APP_SERVER_URL + "/athletes/" + id,  { headers })
                    .then(res => res.json())
                    .then(data => {
                        data.age = this.ageCalculator(Date.parse(data.birthDate));
                        this.setState({ athlete : data})
                    });

                fetch(process.env.REACT_APP_SERVER_URL + "/groups",  { headers })
                    .then(res => res.json())
                    .then(data => {
                        this.setState({ groups : data})
                    });

                fetch(process.env.REACT_APP_SERVER_URL + "/families",  { headers })
                    .then(res => res.json())
                    .then(data => {
                        this.setState({ families : data})
                    });
            }
        }
    }

    handleSubmit(event) {

        event.preventDefault();

        const requestOptions = {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(this.state.athlete)
        }

        fetch(process.env.REACT_APP_SERVER_URL + "/athletes", requestOptions)
            .then(res => res.json())
            .then(data => {
                data.age = this.ageCalculator(Date.parse(data.birthDate));
                this.setState({ athlete : data})
            });
    }

    handleInputChange(event) {

        event.preventDefault();

        const target = event.target;
        const value = target.value;
        const name = target.name;

        let athlete = this.state.athlete
        athlete[name] = value

        this.setState({
            athlete : athlete
        });
    }

    handleInputDateChange(date){

        let athlete = this.state.athlete
        athlete.birthDate = date
        athlete.age = this.ageCalculator(date)

        this.setState({
            athlete : athlete
        })
    }

    handleMultipleSelectChange(event){

        event.preventDefault();

        var options = event.target.options;
        var value = [];
        for (var i = 0, l = options.length; i < l; i++) {
            if (options[i].selected) {
                value.push(options[i].value);
            }
        }
        
        let athlete = this.state.athlete
        athlete.groupIds = value

        this.setState({
            athlete : athlete
        });
    }

    ageCalculator(birth){
        var curr  = new Date();
        var diff = curr.getTime() - birth;
        return Math.max(Math.floor(diff / (1000 * 60 * 60 * 24 * 365.25)), 0)
    }

    render() {
        return (
            <Form onSubmit={this.handleSubmit}>
                <Container>
                    <Row>
                        <Col>
                            <InputGroup>
                                <InputGroup.Prepend>
                                  <InputGroup.Text>Nombre</InputGroup.Text>
                                </InputGroup.Prepend>
                                <Form.Control name="name" type="text" placeholder="Nombre" value={this.state.athlete.name} onChange={this.handleInputChange}/>
                            </InputGroup>
                        </Col>
                        <Col>
                            <InputGroup>
                                <InputGroup.Prepend>
                                  <InputGroup.Text>Familia</InputGroup.Text>
                                </InputGroup.Prepend>
                                <Form.Control name="family" value={this.state.athlete.familyId} as="select" custom onChange={this.handleInputChange}>
                                    {
                                        this.state.families.map(family => {
                                            return (<option key={`family${family.id}`} value={family.id}>{family.firstSurname}</option>)
                                        })
                                    }
                                </Form.Control>
                            </InputGroup>
                        </Col>
                        <Col>
                            <InputGroup>
                                <InputGroup.Prepend>
                                  <InputGroup.Text>Género</InputGroup.Text>
                                </InputGroup.Prepend>
                                <Form.Control name="gender" value={this.state.athlete.gender} as="select" placeholder="Género" custom onChange={this.handleInputChange}>
                                    <option></option>
                                    <option value="male">Hombre</option>
                                    <option value="female">Mujer</option>
                                </Form.Control>
                            </InputGroup>
                        </Col>
                        <Col>
                            <InputGroup>
                                <InputGroup.Prepend>
                                  <InputGroup.Text>Fecha de nacimiento</InputGroup.Text>
                                </InputGroup.Prepend>
                                <DatePicker id="birthDate" 
                                    name="birthDate"
                                    value = {this.state.athlete.birthDate} 
                                    onChange={date => this.handleInputDateChange(date)} 
                                />
                            </InputGroup>
                        </Col>
                        <Col>
                            <InputGroup>
                                <InputGroup.Prepend>
                                    <InputGroup.Text>Edad</InputGroup.Text>
                                </InputGroup.Prepend>
                                <Form.Control name="age" type="number" disabled value={this.state.athlete.age} onChange={this.handleInputChange} />
                            </InputGroup>
                        </Col>
                    </Row>
                    <Row>
                        <Col>
                            <InputGroup>
                                <InputGroup.Prepend>
                                    <InputGroup.Text>Mail</InputGroup.Text>
                                </InputGroup.Prepend>
                                <Form.Control name="mail" type="email" value={this.state.athlete.mail} onChange={this.handleInputChange} />
                            </InputGroup>
                        </Col>
                        <Col>
                            <InputGroup>
                                <InputGroup.Prepend>
                                  <InputGroup.Text>Grupos</InputGroup.Text>
                                </InputGroup.Prepend>
                                <Form.Control name="groups" value={this.state.athlete.groupIds} as="select" multiple onChange={this.handleMultipleSelectChange}>
                                    {
                                        this.state.groups.map(group => {
                                            return (<option key={group.id} value={group.id}>{group.name}</option>)
                                        })
                                    }
                                </Form.Control>
                            </InputGroup>
                        </Col>
                    </Row>
                    <Row>
                        <Col>
                            <InputGroup>
                                <InputGroup.Prepend>
                                    <InputGroup.Text>Dorsal</InputGroup.Text>
                                </InputGroup.Prepend>
                                <Form.Control name="dorsalNumber" type="number" value={this.state.athlete.dorsalNumber} onChange={this.handleInputChange} />
                            </InputGroup>
                        </Col>
                        <Col>
                            <InputGroup>
                                <InputGroup.Prepend>
                                    <InputGroup.Text>Categoria Dorsal</InputGroup.Text>
                                </InputGroup.Prepend>
                                <Form.Control name="dorsalCategory" type="text" value={this.state.athlete.dorsalCategory} onChange={this.handleInputChange} />
                            </InputGroup>
                        </Col>
                        <Col>
                            <InputGroup>
                                <InputGroup.Prepend>
                                    <InputGroup.Text>Categoria</InputGroup.Text>
                                </InputGroup.Prepend>
                                <Form.Control name="category" type="text" value={this.state.athlete.category} onChange={this.handleInputChange} />
                            </InputGroup>
                        </Col>
                        <Col>
                            <InputGroup>
                                <InputGroup.Prepend>
                                    <InputGroup.Text>Licencia</InputGroup.Text>
                                </InputGroup.Prepend>
                                <Form.Control name="license" type="text" value={this.state.athlete.license} onChange={this.handleInputChange} />
                            </InputGroup>
                        </Col>
                    </Row>
                    <Row>
                        <Col>
                            <InputGroup>
                                <InputGroup.Prepend>
                                    <InputGroup.Text>Dirección</InputGroup.Text>
                                </InputGroup.Prepend>
                                <Form.Control name="address" type="text" value={this.state.athlete.address} onChange={this.handleInputChange} />
                            </InputGroup>
                        </Col>
                        <Col>
                            <InputGroup>
                                <InputGroup.Prepend>
                                    <InputGroup.Text>Municipio</InputGroup.Text>
                                </InputGroup.Prepend>
                                <Form.Control name="municipality" type="text" value={this.state.athlete.municipality} onChange={this.handleInputChange} />
                            </InputGroup>
                        </Col>
                        <Col>
                            <InputGroup>
                                <InputGroup.Prepend>
                                    <InputGroup.Text>Código Postal</InputGroup.Text>
                                </InputGroup.Prepend>
                                <Form.Control name="postalCode" type="text" value={this.state.athlete.postalCode} onChange={this.handleInputChange} />
                            </InputGroup>
                        </Col>
                    </Row>
                    <Row>
                        <Button type="submit">Submit</Button>
                    </Row>
                </Container>
            </Form>
        );
    }
}

export default CreateAthlete;
