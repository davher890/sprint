import React from "react";
import "./App.css";
import 'bootstrap/dist/css/bootstrap.min.css';
import CreateUser from './users/CreateUser';
import TableUsers from './users/TableUsers';
import CreateGroup from './groups/CreateGroup';
import TableGroups from './groups/TableGroups';
import TableTrainers from './trainers/TableTrainers';
import TableRunners from './runners/TableRunners';

import { 
  Navbar, Nav, NavDropdown 
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
            <Nav.Link href="/users">Usuarios</Nav.Link>
            <Nav.Link href="/groups">Grupos</Nav.Link>
            <Nav.Link href="/runners">Runners</Nav.Link>
            <Nav.Link href="/trainers">Trainers</Nav.Link>
            <NavDropdown title="Dropdown" id="collasible-nav-dropdown">
              <NavDropdown.Item href="#action/3.1">Action</NavDropdown.Item>
              <NavDropdown.Item href="#action/3.2">Another action</NavDropdown.Item>
              <NavDropdown.Item href="#action/3.3">Something</NavDropdown.Item>
            <NavDropdown.Divider />
              <NavDropdown.Item href="#action/3.4">Separated link</NavDropdown.Item>
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

      <Switch>
        <Route exact path="/user/:id" component={CreateUser} />
        <Route exact path="/user" component={CreateUser}/>
        <Route exact path="/users" component={TableUsers}/>

        <Route exact path="/group/:id" component={CreateGroup} />
        <Route exact path="/group" component={CreateGroup}/>
        <Route exact path="/groups" component={TableGroups}/>
        
        <Route exact path="/trainers" component={TableTrainers}/>
        <Route exact path="/runners" component={TableRunners}/>

      </Switch>
    </Router>

  );
}





