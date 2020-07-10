import React from "react";
import "./App.css";
import 'bootstrap/dist/css/bootstrap.min.css';
import CreateAthlete from './athletes/CreateAthlete';
import TableAthletes from './athletes/TableAthletes';
import CreateFamily from './families/CreateFamily';
import TableFamilies from './families/TableFamilies';
import CreateGroup from './groups/CreateGroup';
import TableGroups from './groups/TableGroups';
import CreateTrainer from './trainers/CreateTrainer';
import TableTrainers from './trainers/TableTrainers';
import CreateSchedule from './schedules/CreateSchedule';
import TableSchedules from './schedules/TableSchedules';
import CreateSportSchool from './sport_schools/CreateSportSchool';
import TableSportSchools from './sport_schools/TableSportSchools';

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
              <Nav.Link href="/sport_schools/list">Escuelas</Nav.Link>
              <Nav.Link href="/athletes/list">Atletas</Nav.Link>
              <Nav.Link href="/families/list">Familias</Nav.Link>
              <NavDropdown title="Entrandores" id="collasible-nav-dropdown">
                <NavDropdown.Item href="/trainers/list">Listado</NavDropdown.Item>
                <NavDropdown.Divider />
                <NavDropdown.Item href="/groups/list">Grupos</NavDropdown.Item>
                <NavDropdown.Divider />
                <NavDropdown.Item href="/schedules/list">Horarios</NavDropdown.Item>
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
            <Route exact path="/sport_schools/list" component={TableSportSchools}/>
            <Route exact path="/sport_schools/:id" component={CreateSportSchool} />
            <Route exact path="/sport_schools" component={CreateSportSchool}/>

            <Route exact path="/athletes/list" component={TableAthletes}/>
            <Route exact path="/athletes/:id" component={CreateAthlete} />
            <Route exact path="/athletes" component={CreateAthlete}/>
            
            <Route exact path="/families/list" component={TableFamilies}/>
            <Route exact path="/families/:id" component={CreateFamily} />
            <Route exact path="/families" component={CreateFamily}/>
            
            <Route exact path="/groups/list" component={TableGroups}/>
            <Route exact path="/groups/:id" component={CreateGroup} />
            <Route exact path="/groups" component={CreateGroup}/>
            
            <Route exact path="/trainers/list" component={TableTrainers}/>
            <Route exact path="/trainers/:id" component={CreateTrainer} />
            <Route exact path="/trainers" component={CreateTrainer}/>
            
            <Route exact path="/schedules/list" component={TableSchedules}/>
            <Route exact path="/schedules/:id" component={CreateSchedule} />
            <Route exact path="/schedules" component={CreateSchedule}/>
            

          </Switch>
        </Container>
      </Router>
    


  );
}





