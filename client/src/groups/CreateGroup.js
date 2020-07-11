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
        	group : {},
            schedules : []
        };
        this.handleInputChange = this.handleInputChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleMultipleSelectChange = this.handleMultipleSelectChange.bind(this);
    }

    componentDidMount(){
                
        const headers = { 'Content-Type': 'application/json' }
        let schedulesPromise = new Promise((resolve, reject) => {
            fetch(process.env.REACT_APP_SERVER_URL + "/schedules/all",  { headers })
                .then(res => res.json())
                .then(data => {
                    this.setState({ schedules : data})
                    resolve()
                });
        })

        let trainersPromise = new Promise((resolve, reject) => {
            fetch(process.env.REACT_APP_SERVER_URL + "/trainers/all",  { headers })
                .then(res => res.json())
                .then(data => {
                    this.setState({ schedules : data})
                    resolve()
                });
        })

        .then((v) => {
            if (this.props.match.params) {
                let id = this.props.match.params.id
                if (id){
                    fetch(process.env.REACT_APP_SERVER_URL + "/groups/" + id,  { headers })
                        .then(res => res.json())
                        .then(data => this.setState({ group : data}));
                }
            }
        })
    }

    handleSubmit(event) {

        event.preventDefault();
        const requestOptions = {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(this.state.group)
        }

        fetch(process.env.REACT_APP_SERVER_URL + "/groups", requestOptions)
            .then(response => console.log(response))
            .then(data => this.setState(data));
    }

    handleInputChange(event) {

        event.preventDefault();

        const target = event.target;
        const value = target.value;
        const name = target.name;

        let group = this.state.group
        group[name] = value

        this.setState({
            group : group
        });
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
        let group = this.state.group
        group.scheduleIds = value

        this.setState({
            group : group
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
                            <Form.Control key="name" id='name' name="name" type="text" value={this.state.group.name} onChange={this.handleInputChange}/>
                        </InputGroup>
                    </Col>
                    <Col>
                        <InputGroup>
                            <InputGroup.Prepend>
                              <InputGroup.Text>Horario</InputGroup.Text>
                            </InputGroup.Prepend>
                            <Form.Control name="schedules" value={this.state.group.scheduleIds} as="select" multiple onChange={this.handleMultipleSelectChange}>
                                {
                                    this.state.schedules.map(sch => {
                                        return <option key={sch.id} value={sch.id}>{sch.day} {sch.startHour}:{sch.startMinute} - {sch.endHour}:{sch.endMinute}</option>
                                    })
                                }
                            </Form.Control>
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
