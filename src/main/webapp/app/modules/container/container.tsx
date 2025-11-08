import './container.scss';

import React, { useState } from 'react';
import { Button, Col, Form, Input, Row, Table } from 'reactstrap';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { searchContainers } from './container.reducer';
import { defaultValue } from "app/shared/model/search-container.model";
import { StatusCode } from "app/shared/model/enumerations/status-code.model";
import { IDownloadContainer } from "app/shared/model/download-object.model";
import { IContainer } from "app/shared/model/container.model";
import { parseStringToJsonObject } from "app/shared/util/entity-utils";

export const SearchContainer = () => {
  const dispatch = useAppDispatch();
  const searchResults = useAppSelector(state => state.container.entities); // Adjust state slice name if different
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
    dispatch(searchContainers(tempCriteria)); // Attach backend search logic here
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


  const mapToDownloadContainer = (item: IContainer): IDownloadContainer => {

    return {
      id: item.name,
      image: item.imageName,
      port: item.port,
      command: item.command ? item.command : undefined,
      env: parseStringToJsonObject(item.environmentVariables),
      volumes: parseStringToJsonObject(item.volumes),
      acceptedPayments: parseStringToJsonObject(item.payments),
    };
  }

  const downloadAsJson = () => {
    if (selectedItems.size === 0) {
      alert('Please select items to download');
      return;
    }

    const selectedData: IDownloadContainer[] = Array.from(selectedItems).map(index => mapToDownloadContainer((searchResults[index])));

    console.log(selectedData);
    const jsonContent = JSON.stringify(selectedData, null, 2);
    const blob = new Blob([jsonContent], {type: 'application/json;charset=utf-8;'});
    const link = document.createElement('a');
    const url = URL.createObjectURL(blob);
    link.setAttribute('href', url);
    link.setAttribute('download', `containers_${ new Date().toISOString().split('T')[0] }.json`);
    link.style.visibility = 'hidden';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };


  return (
      <div className="search-container container-search-section">
        <h2 className="text-center my-4">Search Containers</h2>
        <Form onSubmit={ handleSearch } className="search-form">
          <Row form>
            <Col md={ 4 }>
              <Input
                  type="text"
                  name="searchText"
                  placeholder="Search Text"
                  value={ searchCriteria.searchText }
                  onChange={ handleInputChange }
              />
            </Col>
            <Col md={ 4 }>
              <Input
                  type="text"
                  name="name"
                  placeholder="Name"
                  value={ searchCriteria.name }
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
                  name="walletAddress"
                  placeholder="WalletAddress"
                  value={ searchCriteria.walletAddress }
                  onChange={ handleInputChange }
              />
            </Col>
            {/* <Col md={4}>*/ }
            {/*  <Input*/ }
            {/*    type="select"*/ }
            {/*    name="statusCode"*/ }
            {/*    placeholder="Status Code"*/ }
            {/*    value={searchCriteria.statusCode}*/ }
            {/*    onChange={handleInputChange}*/ }
            {/*  >*/ }
            {/*    <option value="" disabled>*/ }
            {/*      Select Status Code*/ }
            {/*    </option>*/ }
            {/*    {Object.values(StatusCode).map(code => (*/ }
            {/*        <option key={code} value={code}>*/ }
            {/*          {code}*/ }
            {/*        </option>*/ }
            {/*    ))}*/ }

            {/*  </Input>*/ }
            {/* </Col>*/ }
            <Col md={ 4 }>
              <Input
                  type="text"
                  name="createdByUser"
                  placeholder="Created By User"
                  value={ searchCriteria.createdByUserId }
                  onChange={ handleInputChange }
              />
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
                  <Button
                      color="info"
                      size="sm"
                      onClick={ downloadAsJson }
                      disabled={ selectedItems.size === 0 }
                  >
                    Download JSON
                  </Button>
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
                  <th>Name</th>
                  <th>Wallet Address</th>
                  <th>Image Name</th>
                  <th>Port</th>
                  <th>Command</th>
                  <th>Environment Variables</th>
                  <th>Volumes</th>
                  <th>Accepted Payments</th>
                  {/* <th>Status Code</th>*/}
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
                      <td>{ result.name }</td>
                      <td>{ result.walletAddress }</td>
                      <td>{ result.imageName }</td>
                      <td>{ result.port }</td>
                      <td>{ result.command }</td>
                      <td>{ result.environmentVariables }</td>
                      <td>{ result.volumes }</td>
                      <td>{ result.payments }</td>
                      {/* <td>{ result.statusCode }</td>*/}
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

export default SearchContainer;