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
            families : [],
            schedules : [],
            sportSchools : []
        };
        this.handleInputChange = this.handleInputChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleInputDateChange = this.handleInputDateChange.bind(this);
        this.handleMultipleSelectChange = this.handleMultipleSelectChange.bind(this);
        this.handleGroupChange = this.handleGroupChange.bind(this);
        this.fillSchedules = this.fillSchedules.bind(this)
    }

    componentDidMount(){
        const headers = { 'Content-Type': 'application/json' }

        let groupsPromise = new Promise((resolve, reject) => {
            fetch(process.env.REACT_APP_SERVER_URL + "/groups/all",  { headers })
                .then(res => res.json())
                .then(data => {
                    this.setState({ groups : data})
                    resolve()
                });
            })

        let familiesPromise = new Promise((resolve, reject) => {
            fetch(process.env.REACT_APP_SERVER_URL + "/families/all",  { headers })
                .then(res => res.json())
                .then(data => {
                    this.setState({ families : data})
                    resolve()
                });
            })

        let schoolsPromise = new Promise((resolve, reject) => {
            fetch(process.env.REACT_APP_SERVER_URL + "/sport_schools/all",  { headers })
                .then(res => res.json())
                .then(data => {
                    this.setState({ sportSchools : data})
                    resolve()
                });
            })

        Promise.all([groupsPromise, familiesPromise, schoolsPromise]).then(values => {
            if (this.props.match.params.id) {
                let id = this.props.match.params.id
                if (id){
                    fetch(process.env.REACT_APP_SERVER_URL + "/athletes/" + id,  { headers })
                    .then(res => res.json())
                    .then(data => {
                        data.age = this.ageCalculator(Date.parse(data.birthDate));

                        if (data.groupId){
                            this.fillSchedules(data.groupId)
                        }
                        this.setState({ athlete : data})
                    });
                }
            }
        })
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

    fillSchedules(groupId){
        const headers = { 'Content-Type': 'application/json' }
        fetch(process.env.REACT_APP_SERVER_URL + "/groups/" + groupId + "/schedules",  { headers })
            .then(res => res.json())
            .then(data => {
                this.setState({ schedules : data})
            });
    }

    handleGroupChange(event) {

        this.handleInputChange(event)
        event.preventDefault();

        const target = event.target;
        const value = target.value;

        this.fillSchedules(value)
        
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
        athlete.scheduleIds = value

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
                <Container fluid>
                    <Form.Group>
                        <Row>
                            <Col>
                                <InputGroup>
                                    <InputGroup.Prepend>
                                      <InputGroup.Text>Escuala Deportiva</InputGroup.Text>
                                    </InputGroup.Prepend>
                                    <Form.Control name="sportSchools" value={this.state.athlete.sportSchoolId} as="select" custom onChange={this.handleInputChange}>
                                        {
                                            this.state.sportSchools.map(spe => {
                                                return (<option key={`spe${spe.id}`} value={spe.id}>{spe.name}</option>)
                                            })
                                        }
                                    </Form.Control>
                                </InputGroup>
                            </Col>
                            <Col>
                                <InputGroup>
                                    <InputGroup.Prepend>
                                      <InputGroup.Text>Nombre</InputGroup.Text>
                                    </InputGroup.Prepend>
                                    <Form.Control name="name" type="text" value={this.state.athlete.name} onChange={this.handleInputChange}/>
                                </InputGroup>
                            </Col>
                            <Col>
                                <InputGroup>
                                    <InputGroup.Prepend>
                                      <InputGroup.Text>Dni</InputGroup.Text>
                                    </InputGroup.Prepend>
                                    <Form.Control name="dni" type="text" value={this.state.athlete.dni} onChange={this.handleInputChange}/>
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
                        </Row>
                    </Form.Group>
                    <Form.Group>
                        <Row>
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
                            <Col md="auto">
                                <InputGroup>
                                    <InputGroup.Prepend>
                                        <InputGroup.Text>Edad</InputGroup.Text>
                                    </InputGroup.Prepend>
                                    <Form.Control name="age" type="number" disabled value={this.state.athlete.age} onChange={this.handleInputChange} />
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
                        </Row>
                    </Form.Group>
                    <Form.Group>
                        <Row>
                            <Col>
                                <InputGroup>
                                    <InputGroup.Prepend>
                                        <InputGroup.Text>Categoria</InputGroup.Text>
                                    </InputGroup.Prepend>
                                    <Form.Control name="category" value={this.state.athlete.category} as="select" onChange={this.handleInputChange}>
                                        <option value="SENIOR">MASTER</option>
                                        <option value="SENIOR">SENIOR</option>
                                        <option value="SUB23">SUB23</option>
                                        <option value="SUB20">SUB20</option>
                                        <option value="SUB18">SUB18</option>
                                        <option value="SUB16">SUB16</option>
                                        <option value="SUB14">SUB14</option>
                                        <option value="ALEVIN">ALEVIN</option>
                                        <option value="BENJAMIN">BENJAMIN</option>
                                        <option value="MINI">MINI</option>
                                    </Form.Control>
                                </InputGroup>
                            </Col>
                            <Col>
                                <InputGroup>
                                    <InputGroup.Prepend>
                                        <InputGroup.Text>Categoria Dorsal</InputGroup.Text>
                                    </InputGroup.Prepend>
                                    <Form.Control name="dorsalCategory" value={this.state.athlete.dorsalCategory} as="select" onChange={this.handleInputChange} >
                                        <option value="SM">SM</option>
                                        <option value="SF">SF</option>
                                        <option value="U23M">U23M</option>
                                        <option value="U20F">U20F</option>
                                        <option value="U18F">U18F</option>
                                        <option value="U20M">U20M</option>
                                        <option value="U16M">U16M</option>
                                        <option value="U23F">U23F</option>
                                        <option value="U14M">U14M</option>
                                        <option value="U10M">U10M</option>
                                        <option value="U14F">U14F</option>
                                        <option value="U10F">U10F</option>
                                        <option value="U18M">U18M</option>
                                        <option value="U12M">U12M</option>
                                    </Form.Control>                              
                                </InputGroup>
                            </Col>
                            <Col md="auto">
                                <InputGroup>
                                    <InputGroup.Prepend>
                                        <InputGroup.Text>Dorsal</InputGroup.Text>
                                    </InputGroup.Prepend>
                                    <Form.Control name="dorsalNumber" type="number" value={this.state.athlete.dorsalNumber} onChange={this.handleInputChange} />
                                </InputGroup>
                            </Col>
                        </Row>
                    <Form.Group>
                    </Form.Group>
                        <Row>
                            <Col md="auto">
                                <InputGroup>
                                    <InputGroup.Prepend>
                                        <InputGroup.Text>Tipo de Licencia</InputGroup.Text>
                                    </InputGroup.Prepend>
                                    <Form.Control name="licenseType" value={this.state.athlete.licenseType} as="select" onChange={this.handleInputChange}>
                                        <option></option>
                                        <option value="National">Nacional</option>
                                        <option value="Territorial">Territorial</option>
                                    </Form.Control>
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
                            <Col md="auto">
                                <InputGroup>
                                    <InputGroup.Prepend>
                                        <InputGroup.Text>Tipo de Cuota</InputGroup.Text>
                                    </InputGroup.Prepend>
                                    <Form.Control name="feeType" value={this.state.athlete.feeType} as="select" onChange={this.handleInputChange}>
                                        <option></option>
                                        <option value="CLUB">Club</option>
                                        <option value="PISTAS">Pistas</option>
                                        <option value="LICENCIA">Licencia</option>
                                        <option value="LIC/PISTAS">Lic. Pistas</option>
                                        <option value="ENTRENADOR">Entrandor</option>
                                    </Form.Control>
                                </InputGroup>
                            </Col>
                        </Row>
                    </Form.Group>
                    <Form.Group>
                        <Row>
                            <Col>
                                <InputGroup>
                                    <InputGroup.Prepend>
                                      <InputGroup.Text>Aut. Imágenes</InputGroup.Text>
                                    </InputGroup.Prepend>
                                    <Form.Control name="imageAuth" value={this.state.athlete.imageAuth} as="select" custom onChange={this.handleInputChange}>
                                        <option></option>
                                        <option value="false">No</option>
                                        <option value="true">Si</option>
                                    </Form.Control>
                                </InputGroup>
                            </Col>
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
                                        <InputGroup.Text>Teléfono</InputGroup.Text>
                                    </InputGroup.Prepend>
                                    <Form.Control name="phone1" type="tel" value={this.state.athlete.phone1} onChange={this.handleInputChange} />
                                </InputGroup>
                            </Col>
                            <Col>
                                <InputGroup>
                                    <InputGroup.Prepend>
                                        <InputGroup.Text>Teléfono 2</InputGroup.Text>
                                    </InputGroup.Prepend>
                                    <Form.Control name="phone2" type="tel" value={this.state.athlete.phone2} onChange={this.handleInputChange} />
                                </InputGroup>
                            </Col>
                            <Col>
                                <InputGroup>
                                    <InputGroup.Prepend>
                                        <InputGroup.Text>Teléfono 3</InputGroup.Text>
                                    </InputGroup.Prepend>
                                    <Form.Control name="phone3" type="tel" value={this.state.athlete.phone3} onChange={this.handleInputChange} />
                                </InputGroup>
                            </Col>
                        </Row>
                    </Form.Group>
                    <Form.Group>
                        <Row>
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
                            <Col>
                                <InputGroup>
                                    <InputGroup.Prepend>
                                        <InputGroup.Text>Dirección</InputGroup.Text>
                                    </InputGroup.Prepend>
                                    <Form.Control name="address" type="text" value={this.state.athlete.address} onChange={this.handleInputChange} />
                                </InputGroup>
                            </Col>
                        </Row>
                    </Form.Group>
                    <Form.Group>
                        <Row>
                            <Col>
                                <InputGroup>
                                    <InputGroup.Prepend>
                                      <InputGroup.Text>Grupos</InputGroup.Text>
                                    </InputGroup.Prepend>
                                    <Form.Control name="groupId" value={this.state.athlete.groupId} as="select" onChange={this.handleGroupChange}>
                                        {
                                            this.state.groups.map(group => {
                                                return (<option key={group.id} value={group.id}>{group.name}</option>)
                                            })
                                        }
                                    </Form.Control>
                                </InputGroup>
                            </Col>
                            <Col>
                                <InputGroup>
                                    <InputGroup.Prepend>
                                      <InputGroup.Text>Horarios</InputGroup.Text>
                                    </InputGroup.Prepend>
                                    <Form.Control name="scheduleIds" value={this.state.athlete.scheduleIds} as="select" multiple onChange={this.handleMultipleSelectChange}>
                                        {
                                            this.state.schedules.map(sch => {
                                                return (<option key={sch.id} value={sch.id}>{sch.day} - {sch.startHour}:{sch.startMinute} - {sch.endHour}:{sch.endMinute}</option>)
                                            })
                                        }
                                    </Form.Control>
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

export default CreateAthlete;
