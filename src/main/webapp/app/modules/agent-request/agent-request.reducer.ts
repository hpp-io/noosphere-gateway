import axios from 'axios';
import { createAsyncThunk, isFulfilled, isPending, isRejected } from '@reduxjs/toolkit';
import { createEntitySlice, EntityState } from "app/shared/reducers/reducer.utils";
import { defaultValue, IAgentRequest } from "app/shared/model/agent-request.model";
import { ISearchAgentRequest } from "app/shared/model/search-agent-request.model";

const initialState: EntityState<IAgentRequest> = {
  loading: false,
  errorMessage: null,
  entities: [],
  entity: defaultValue,
  updating: false,
  totalItems: 0,
  updateSuccess: false,
};

const apiUrl = 'api/agent-requests';

export const searchAgentRequests = createAsyncThunk('agent-request/fetch_entities',
    async (entity: ISearchAgentRequest, thunkAPI) => {
      const requestUrl = `${ apiUrl }/search${ entity.sort ? `?page=${ entity.page }&size=${ entity.size }&sort=${ entity.sort }&` : '?' }cacheBuster=${ new Date().getTime() }`;
      const requestBody = {
        agentName: entity.agentName,
        containerId: entity.containerId,
        agentId: entity.agentId,
        statusCode: entity.statusCode,
      };
      return axios.post<IAgentRequest[]>(requestUrl, requestBody);
    });

export const createAgentRequest = createAsyncThunk('agent-request/create_entity',
    async (entity: Omit<IAgentRequest, 'id'>, thunkAPI) => {
      const result = await axios.post<IAgentRequest>(apiUrl, entity);
      return result;
    });

export const AgentRequestSlice = createEntitySlice({
  name: 'agentRequest',
  initialState,
  reducers: {},
  extraReducers(builder) {
    builder
    .addMatcher(isFulfilled(searchAgentRequests), (state, action) => {
      state.loading = false;
      state.entities = action.payload.data;
      state.totalItems = parseInt(action.payload.headers['x-total-count'], 10);
    })
    .addMatcher(isPending(searchAgentRequests), state => {
      state.errorMessage = null;
      state.updateSuccess = false;
      state.loading = true;
    })
    .addMatcher(isFulfilled(createAgentRequest), (state, action) => {
      state.loading = false;
      state.updating = false;
      state.updateSuccess = true;
      state.entity = action.payload.data;
    })
    .addMatcher(isPending(createAgentRequest), state => {
      state.errorMessage = null;
      state.updateSuccess = false;
      state.updating = true;
      state.loading = true;
    })
    .addMatcher(isRejected(searchAgentRequests, createAgentRequest), (state, action) => {
      state.loading = false;
      state.updating = false;
      state.updateSuccess = false;
      state.errorMessage = action.error.message;
    })
    ;
  },
});

export default AgentRequestSlice.reducer;