import React, { Component } from "react";
import {
    Form, 
    Row,
    Container
} from 'react-bootstrap';
import "react-datepicker/dist/react-datepicker.css";
import 'bootstrap/dist/css/bootstrap.min.css';
import DatePicker from 'react-datepicker'


class TimeRangePicker extends Component {

	constructor(props) {
        super(props);
        this.state = {
        	intervals : 15,
            startTime : new Date(),
            endTime : new Date()
        }
    }

    setStartTime(time) {
        this.setState({
            startTime : time
        })
    }

    setEndTime(time) {
        this.setState({
            endTime : time
        })
    }   

	render() {
		return (
			<Container key={(new Date).getTime()}>
				<Row>
					<Form.Group >
                        <Form.Control name='week_day' id={(new Date).getTime()} as="select" custom>
                            <option>Lunes</option>
                            <option>Martes</option>
                            <option>Miercoles</option>
                            <option>Jueves</option>
                            <option>Viernes</option>
                            <option>Sabado</option>
                            <option>Domingo</option>
                        </Form.Control>
                    
                        <DatePicker
                            name='start_time'
                            selected={this.state.startTime}
                            onChange={date => this.setStartTime(date)}
                            showTimeSelect
                            showTimeSelectOnly
                            timeIntervals={this.state.intervals}
                            timeCaption="Time"
                            dateFormat="h:mm aa"
                        />
                        <DatePicker
                            name='end_time'
                            selected={this.state.endTime}
                            onChange={date => this.setEndTime(date)}
                            showTimeSelect
                            showTimeSelectOnly
                            timeIntervals={this.state.intervals}
                            timeCaption="Time"
                            dateFormat="h:mm aa"
                        />
                    </Form.Group>
		        </Row>
	        </Container>
        )
	}
}

export default TimeRangePicker;
