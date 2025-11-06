import './validator.scss';

import React, { useState } from 'react';
import { Button, Input, Form, Row, Col, Table } from 'reactstrap';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { searchValidators } from './validator.reducer';
import { defaultValue } from "app/shared/model/search-validator.model";
import { StatusCode } from "app/shared/model/enumerations/status-code.model";
import { IDownloadValidator } from "app/shared/model/download-validator.model"; // Example reducer

export const SearchValidator = () => {
  const dispatch = useAppDispatch();
  const searchResults = useAppSelector(state => state.validator.entities); // Adjust state slice name if different
  const [searchCriteria, setSearchCriteria] = useState(defaultValue);
  const [selectedItems, setSelectedItems] = useState<Set<number>>(new Set());
  const [selectAll, setSelectAll] = useState(false);


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
    dispatch(searchValidators(tempCriteria)); // Attach backend search logic here
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


  const mapToDownloadValidator = (item: any): IDownloadValidator => ({
    id: item.id,
    name: item.name,
    image: item.image,
    external: item.external,
    port: item.port,
    command: item.command,
    parameters: item.parameters,
    generatesProofs: item.generatesProofs,
    price: item.price
  });

  const downloadAsJson = () => {
    if (selectedItems.size === 0) {
      alert('Please select items to download');
      return;
    }

    const selectedData:  IDownloadValidator[] = Array.from(selectedItems).map(index => mapToDownloadValidator((searchResults[index])));

    const jsonContent = JSON.stringify(selectedData, null, 2);
    const blob = new Blob([jsonContent], { type: 'application/json;charset=utf-8;' });
    const link = document.createElement('a');
    const url = URL.createObjectURL(blob);
    link.setAttribute('href', url);
    link.setAttribute('download', `validators_${new Date().toISOString().split('T')[0]}.json`);
    link.style.visibility = 'hidden';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };




  return (
    <div className="search-validator validator-search-section">
      <h2 className="text-center my-4">Search Validators</h2>
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
          <>
            <div className="d-flex justify-content-between align-items-center mt-4 mb-3">
              <div>
              <span className="text-muted">
                {selectedItems.size} of {searchResults.length} items selected
              </span>
              </div>
              <div>
                <Button
                    color="info"
                    size="sm"
                    onClick={downloadAsJson}
                    disabled={selectedItems.size === 0}
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
                      checked={selectAll}
                      onChange={handleSelectAll}
                      title="Select All"
                  />
                </th>
                <th>#</th>
                <th>Name</th>
                <th>Description</th>
                <th>Price</th>
                <th>Parameters</th>
              </tr>
              </thead>
              <tbody>
              {searchResults.map((result, index) => (
                  <tr key={index} className={selectedItems.has(index) ? 'table-active' : ''}>
                    <td>
                      <Input
                          type="checkbox"
                          checked={selectedItems.has(index)}
                          onChange={() => handleSelectItem(index)}
                      />
                    </td>
                    <td>{index + 1}</td>
                    <td>{result.name}</td>
                    <td>{result.description}</td>
                    <td>{result.price}</td>
                    <td>{result.parameters}</td>
                  </tr>
              ))}
              </tbody>
            </Table>
          </>
      )}

      {searchResults && searchResults.length === 0 && (
          <div className="text-center mt-4">No results found.</div>
      )}

    </div>
  );
};

export default SearchValidator;