import React from "react";
import "./App.css";
import 'bootstrap/dist/css/bootstrap.min.css';
import CreateAthlete from './athletes/CreateAthlete';
import TableAthletes from './athletes/TableAthletes';
import CreateGroup from './groups/CreateGroup';
import TableGroups from './groups/TableGroups';
import CreateTrainer from './trainers/CreateTrainer';
import TableTrainers from './trainers/TableTrainers';
import CreateSchedule from './schedules/CreateSchedule';
import TableSchedules from './schedules/TableSchedules2';

import { 
  Navbar, Nav, NavDropdown, Container 
} from 'react-bootstrap';

import {
    BrowserRouter as Router,
    Switch,
    Route
} from "react-router-dom";



export default function App() {
return (
    
      <Router>
        <Navbar collapseOnSelect expand="lg" bg="dark" variant="dark">
          <Navbar.Brand href="/">Sprint</Navbar.Brand>
          <Navbar.Toggle aria-controls="responsive-navbar-nav" />
          <Navbar.Collapse id="responsive-navbar-nav">
            <Nav className="mr-auto">
              <Nav.Link href="/athletes">Atletas</Nav.Link>
              <Nav.Link href="/groups">Grupos</Nav.Link>
              <NavDropdown title="Entrandores" id="collasible-nav-dropdown">
                <NavDropdown.Item href="/trainers">Listado</NavDropdown.Item>
                <NavDropdown.Divider />
                <NavDropdown.Item href="/groups">Grupos</NavDropdown.Item>
                <NavDropdown.Divider />
                <NavDropdown.Item href="schedules">Horarios</NavDropdown.Item>
              </NavDropdown>
            </Nav>
            <Nav>
              <Nav.Link href="#deets">More deets</Nav.Link>
              <Nav.Link eventKey={2} href="#memes">
                  Dank memes
              </Nav.Link>
            </Nav>
          </Navbar.Collapse>
        </Navbar>
        <Container>
          <Switch>
            <Route exact path="/athete/:id" component={CreateAthlete} />
            <Route exact path="/athete" component={CreateAthlete}/>
            <Route exact path="/athetes" component={TableAthletes}/>

            <Route exact path="/group/:id" component={CreateGroup} />
            <Route exact path="/group" component={CreateGroup}/>
            <Route exact path="/groups" component={TableGroups}/>

            <Route exact path="/trainer/:id" component={CreateTrainer} />
            <Route exact path="/trainer" component={CreateTrainer}/>
            <Route exact path="/trainers" component={TableTrainers}/>
            
            <Route exact path="/schedule/:id" component={CreateSchedule} />
            <Route exact path="/schedule" component={CreateSchedule}/>
            <Route exact path="/schedules" component={TableSchedules}/>
            

          </Switch>
        </Container>
      </Router>
    


  );
}





