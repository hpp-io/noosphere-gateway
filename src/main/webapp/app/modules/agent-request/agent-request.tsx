import './agent-request.scss';

import React, { useState } from 'react';
import { Button, Col, Form, Input, Row, Table } from 'reactstrap';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { searchAgentRequests } from './agent-request.reducer';
import { defaultValue } from "app/shared/model/search-agent-request.model";
import { StatusCode } from "app/shared/model/enumerations/status-code.model";
import { IAgentRequest } from "app/shared/model/agent-request.model";
import { parseStringToJsonObject } from "app/shared/util/entity-utils";

export const SearchAgentRequest = () => {
  const dispatch = useAppDispatch();
  const searchResults = useAppSelector(state => state.agentRequest.entities); // Adjust state slice name if different
  const [searchCriteria, setSearchCriteria] = useState(defaultValue);
  const [selectedItems, setSelectedItems] = useState<Set<number>>(new Set());
  const [selectAll, setSelectAll] = useState(false);


  const handleInputChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const {name, value} = event.target;
    setSearchCriteria({...searchCriteria, [name]: value});
  };

  const handleSearch = event => {

    event.preventDefault();
    const tempCriteria = {...searchCriteria};
    if (!tempCriteria.sort) {
      tempCriteria.sort = "name,asc";
    }
    if (!tempCriteria.statusCode) {
      tempCriteria.statusCode = StatusCode.ACTIVE;
    }
    dispatch(searchAgentRequests(tempCriteria)); // Attach backend search logic here
  };

  const handleSelectItem = (index: number) => {
    const newSelectedItems = new Set(selectedItems);
    if (newSelectedItems.has(index)) {
      newSelectedItems.delete(index);
    } else {
      newSelectedItems.add(index);
    }
    setSelectedItems(newSelectedItems);
    setSelectAll(newSelectedItems.size === searchResults.length);
  };

  const handleSelectAll = () => {
    if (selectAll) {
      setSelectedItems(new Set());
      setSelectAll(false);
    } else {
      const allIndices: Set<any> = new Set(searchResults.map((_, index) => index));
      setSelectedItems(allIndices);
      setSelectAll(true);
    }
  };






  return (
      <div className="search-agentRequest agentRequest-search-section">
        <h2 className="text-center my-4">Search AgentRequests</h2>
        <Form onSubmit={ handleSearch } className="search-form">
          <Row form>
            <Col md={ 4 }>
              <Input
                  type="text"
                  name="agentName"
                  placeholder="Agent Name"
                  value={ searchCriteria.agentName }
                  onChange={ handleInputChange }
              />
            </Col>
            <Col md={ 4 }>
              <Input
                  type="text"
                  name="containerId"
                  placeholder="Container Id"
                  value={ searchCriteria.containerId }
                  onChange={ handleInputChange }
              />
            </Col>
            <Col md={ 1 }>
            </Col>
          </Row>
          <Row form className="mt-3">
            <Col md={ 4 }>
              <Input
                  type="text"
                  name="agentId"
                  placeholder="Agent Id"
                  value={ searchCriteria.agentId }
                  onChange={ handleInputChange }
              />
            </Col>
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
            <Col md={ 1 }>
              <Button className="mt-4" color="primary" type="submit">
                Search
              </Button>
            </Col>
          </Row>
        </Form>

        { searchResults && searchResults.length > 0 && (
            <>
              <div className="d-flex justify-content-between align-items-center mt-4 mb-3">
                <div>
              <span className="text-muted">
                { selectedItems.size } of { searchResults.length } items selected
              </span>
                </div>
                <div>
                  {/* <Button*/}
                  {/*    color="info"*/}
                  {/*    size="sm"*/}
                  {/*    onClick={ downloadAsJson }*/}
                  {/*    disabled={ selectedItems.size === 0 }*/}
                  {/* >*/}
                  {/*  Download JSON*/}
                  {/* </Button>*/}
                </div>
              </div>

              <Table responsive striped className="search-result-table">
                <thead>
                <tr>
                  <th>
                    <Input
                        type="checkbox"
                        checked={ selectAll }
                        onChange={ handleSelectAll }
                        title="Select All"
                    />
                  </th>
                  <th>#</th>
                  <th>Status Code</th>
                  <th>Agent</th>
                  <th>Container</th>
                  <th>User Subscription</th>
                </tr>
                </thead>
                <tbody>
                { searchResults.map((result, index) => (
                    <tr key={ index } className={ selectedItems.has(index) ? 'table-active' : '' }>
                      <td>
                        <Input
                            type="checkbox"
                            checked={ selectedItems.has(index) }
                            onChange={ () => handleSelectItem(index) }
                        />
                      </td>
                      <td>{ index + 1 }</td>
                      <td>{ result.statusCode }</td>
                      <td>{ result.agent }</td>
                      <td>{ result.container }</td>
                      <td>{ result.userSubscription }</td>
                    </tr>
                )) }
                </tbody>
              </Table>
            </>
        ) }

        { searchResults && searchResults.length === 0 && (
            <div className="text-center mt-4">No results found.</div>
        ) }

      </div>
  );
};

export default SearchAgentRequest;