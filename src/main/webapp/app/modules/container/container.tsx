import './container.scss';

import React, { useState } from 'react';
import { Button, Input, Form, Row, Col, Table } from 'reactstrap';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { searchContainers } from './container.reducer';
import { defaultValue } from "app/shared/model/search-container.model";
import { StatusCode } from "app/shared/model/enumerations/status-code.model"; // Example reducer

export const SearchContainer = () => {
  const dispatch = useAppDispatch();
  const searchResults = useAppSelector(state => state.container.entities); // Adjust state slice name if different
  const [searchCriteria, setSearchCriteria] = useState(defaultValue);

  const handleInputChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = event.target;
    setSearchCriteria({...searchCriteria, [name]: value});
  };

  const handleSearch = event => {

    event.preventDefault();
    const tempCriteria = { ...searchCriteria };
    if (!tempCriteria.sort) {
      tempCriteria.sort = "name,asc";
    }
    if (!tempCriteria.statusCode) {
      tempCriteria.statusCode = StatusCode.ACTIVE;
    }
    dispatch(searchContainers(tempCriteria)); // Attach backend search logic here
  };

  return (
    <div className="search-container container-search-section">
      <h2 className="text-center my-4">Search Containers</h2>
      <Form onSubmit={handleSearch} className="search-form" >
        <Row form>
          <Col md={4}>
            <Input
              type="text"
              name="name"
              placeholder="Name"
              value={searchCriteria.name}
              onChange={handleInputChange}
            />
          </Col>
          <Col md={4}>
            <Input
              type="text"
              name="searchText"
              placeholder="Search Text"
              value={searchCriteria.searchText}
              onChange={handleInputChange}
            />
          </Col>
          <Col md={4}>
            <Input
              type="number"
              name="price"
              placeholder="Price"
              value={searchCriteria.price}
              onChange={handleInputChange}
            />
          </Col>
        </Row>
        <Row form className="mt-3">
          <Col md={4}>
            <Input
              type="select"
              name="statusCode"
              placeholder="Status Code"
              value={searchCriteria.statusCode}
              onChange={handleInputChange}
            >
              <option value="" disabled>
                Select Status Code
              </option>
              {Object.values(StatusCode).map(code => (
                  <option key={code} value={code}>
                    {code}
                  </option>
              ))}

            </Input>
          </Col>
          <Col md={4}>
            <Input
              type="text"
              name="createdByUser"
              placeholder="Created By User"
              value={searchCriteria.createdByUserId}
              onChange={handleInputChange}
            />
          </Col>
        </Row>
        <Button className="mt-4" color="primary" type="submit">
          Search
        </Button>
      </Form>

      {searchResults && searchResults.length > 0 && (
        <Table responsive striped className="search-result-table mt-4">
          <thead>
            <tr>
              <th>#</th>
              <th>Name</th>
              <th>Description</th>
              <th>Price</th>
              <th>Parameters</th>
              {/* <th>Status Code</th>*/}
              {/* <th>Created By</th>*/}
            </tr>
          </thead>
          <tbody>
            {searchResults.map((result, index) => (
              <tr key={index}>
                <td>{index + 1}</td>
                <td>{result.name}</td>
                <td>{result.description}</td>
                <td>{result.price}</td>
                <td>{result.parameters}</td>
                {/* <td>{result.statusCode}</td>*/}
                {/* <td>{result.createdByUserId}</td>*/}
              </tr>
            ))}
          </tbody>
        </Table>
      )}

      {searchResults && searchResults.length === 0 && (
        <div className="text-center mt-4">No results found.</div>
      )}
    </div>
  );
};

export default SearchContainer;