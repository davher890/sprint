import React, { Component } from "react";
import {
    Form, 
    Button,
    Row,
    Container
} from 'react-bootstrap';
import TimeRangePicker from '../utils/TimeRangePicker'

class CreateGroup extends Component {
    constructor(props) {
        super(props);
        this.state = {
        	id : 0,
            name : "",
            days : []
        };
        this.startDate = new Date()
        this.handleInputChange = this.handleInputChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
        this.addDay = this.addDay.bind(this);
    }

    componentDidMount(){
        if (this.props.match.params) {
            let id = this.props.match.params.id
            this.setState({ 
                id : id
            })

            const headers = { 'Content-Type': 'application/json' }
            fetch("http://localhost:9000/groups/" + id,  { headers })
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

        fetch('http://localhost:9000/groups', requestOptions)
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

    addDay(event) {
        event.preventDefault();
        console.log('event')
        this.setState({days : this.state.days.concat({week_day:'Lunes'})})
    }

    render() {
        return (
            <Form onSubmit={this.handleSubmit}>
                <Container>
                    <Row>
                        <Form.Group>
                            <Form.Control id='name' name="name" type="text" placeholder="Name" value={this.state.name} onChange={this.handleInputChange}/>
                        </Form.Group>
                    </Row>
                    <Row>
                        <Button onClick={this.addDay}>Añadir horario</Button>
                        {
                            this.state.days.map((day, i) => 

                                (<TimeRangePicker key={i} name={`range_${i}`}/>)
                            )
                        }
                    </Row>
                    <Row>
                        <Button type="submit">Submit</Button>
                    </Row>
                </Container>
            </Form>
        );
    }
}

export default CreateGroup;
