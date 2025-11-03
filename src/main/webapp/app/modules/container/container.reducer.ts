import axios from 'axios';
import { createAsyncThunk, createSlice, isFulfilled, isPending } from '@reduxjs/toolkit';
import { createEntitySlice, EntityState } from "app/shared/reducers/reducer.utils";
import { IContainer, defaultValue } from "app/shared/model/container.model";
import { ISearchContainer } from "app/shared/model/search-container.model";

const initialState: EntityState<IContainer> = {
  loading: false,
  errorMessage: null,
  entities: [],
  entity: defaultValue,
  updating: false,
  totalItems: 0,
  updateSuccess: false,
};

const apiUrl = 'api/containers';

export const searchContainers = createAsyncThunk('container/fetch_entities',
    async (entity: ISearchContainer, thunkAPI) => {
  const requestUrl = `${apiUrl}/search${entity.sort ? `?page=${entity.page}&size=${entity.size}&sort=${entity.sort}&` : '?'}cacheBuster=${new Date().getTime()}`;
  const requestBody = {
    name: entity.name,
    searchText: entity.searchText,
    price: entity.price,
    statusCode: entity.statusCode,
    createdByUserId: entity.createdByUserId,
  };
  const response = await axios.post(requestUrl, requestBody);
  return response.data;
});

export const ContainerSlice = createEntitySlice({
  name: 'container',
  initialState,
  reducers: {},
  extraReducers(builder) {
    builder
    .addMatcher(isFulfilled(searchContainers), (state, action) => {
      return {
        ...state,
        loading: false,
        entities: action.payload.data,
        totalItems: parseInt(action.payload.headers['x-total-count'], 10),
      };
    })
    .addMatcher(isPending(searchContainers), state => {
      state.errorMessage = null;
      state.updateSuccess = false;
      state.loading = true;
    })
    ;
  },
});

export default ContainerSlice.reducer;