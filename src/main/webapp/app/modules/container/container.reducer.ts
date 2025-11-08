import axios from 'axios';
import { createAsyncThunk, isFulfilled, isPending, isRejected } from '@reduxjs/toolkit';
import { createEntitySlice, EntityState } from "app/shared/reducers/reducer.utils";
import { defaultValue, IContainer } from "app/shared/model/container.model";
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
      const requestUrl = `${ apiUrl }/search${ entity.sort ? `?page=${ entity.page }&size=${ entity.size }&sort=${ entity.sort }&` : '?' }cacheBuster=${ new Date().getTime() }`;
      const requestBody = {
        name: entity.name,
        searchText: entity.searchText,
        walletAddress: entity.walletAddress,
        statusCode: entity.statusCode,
        createdByUserId: entity.createdByUserId,
      };
      return axios.post<IContainer[]>(requestUrl, requestBody);
    });

export const createContainer = createAsyncThunk('container/create_entity',
    async (entity: Omit<IContainer, 'id'>, thunkAPI) => {
      const result = await axios.post<IContainer>(apiUrl, entity);
      return result;
    });

export const ContainerSlice = createEntitySlice({
  name: 'container',
  initialState,
  reducers: {},
  extraReducers(builder) {
    builder
    .addMatcher(isFulfilled(searchContainers), (state, action) => {
      state.loading = false;
      state.entities = action.payload.data;
      state.totalItems = parseInt(action.payload.headers['x-total-count'], 10);
    })
    .addMatcher(isPending(searchContainers), state => {
      state.errorMessage = null;
      state.updateSuccess = false;
      state.loading = true;
    })
    .addMatcher(isFulfilled(createContainer), (state, action) => {
      state.loading = false;
      state.updating = false;
      state.updateSuccess = true;
      state.entity = action.payload.data;
    })
    .addMatcher(isPending(createContainer), state => {
      state.errorMessage = null;
      state.updateSuccess = false;
      state.updating = true;
      state.loading = true;
    })
    .addMatcher(isRejected(searchContainers, createContainer), (state, action) => {
      state.loading = false;
      state.updating = false;
      state.updateSuccess = false;
      state.errorMessage = action.error.message;
    })
    ;
  },
});

export default ContainerSlice.reducer;