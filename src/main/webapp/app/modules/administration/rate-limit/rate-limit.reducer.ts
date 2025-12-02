import axios from 'axios';
import { createAsyncThunk, createSlice, isFulfilled, isPending, isRejected } from '@reduxjs/toolkit';
import { cleanEntity } from 'app/shared/util/entity-utils';
import { serializeAxiosError } from 'app/shared/reducers/reducer.utils';

export interface IRateLimit {
  apiKey: string;
  availableTokens: number;
  callsPerSecond: number;
  callsPerMinute: number;
  callsPerHour: number;
  callsPerDay: number;
}

const initialState = {
  loading: false,
  errorMessage: null,
  rateLimits: [] as IRateLimit[],
  rateLimit: {} as IRateLimit,
  updating: false,
  updateSuccess: false,
  totalItems: 0,
};

const apiUrl = '/api/admin/rate-limits';

// Actions

export const searchRateLimits = createAsyncThunk(
    'rate-limiting/fetch_rate_limits',
    async (params: {
      apiKey?: string;
      page?: number;
      size?: number;
      sort?: string;
    }) => {

      const {page, size, sort, ...filters} = params;
      const requestUrl = `${ apiUrl }/search`;

      const cleanFilters = Object.fromEntries(Object.entries(filters).filter(([_, v]) => v != null && v !== ''));

      return axios.post<IRateLimit[]>(requestUrl, cleanFilters, {
        params: {page, size, sort},
      });
    },
    {serializeError: serializeAxiosError}
);

export const getRateLimit = createAsyncThunk(
    'rate-limiting/fetch_rate_limit',
    async (apiKey: string) => {
      const requestUrl = `${ apiUrl }/${ apiKey }`;
      return axios.get<IRateLimit>(requestUrl);
    },
    {serializeError: serializeAxiosError}
);

export const createRateLimit = createAsyncThunk(
    'rate-limiting/create_rate_limit',
    async (rateLimit: IRateLimit, thunkAPI) => {
      const result = await axios.post<IRateLimit>(apiUrl, cleanEntity(rateLimit));
      thunkAPI.dispatch(searchRateLimits({apiKey: ''}));
      return result;
    },
    {serializeError: serializeAxiosError}
);

export const updateRateLimit = createAsyncThunk(
    'rate-limiting/update_rate_limit',
    async (rateLimit: IRateLimit, thunkAPI) => {
      const result = await axios.put<IRateLimit>(`${ apiUrl }/${ rateLimit.apiKey }`, cleanEntity(rateLimit));
      thunkAPI.dispatch(searchRateLimits({apiKey: ''}));
      return result;
    },
    {serializeError: serializeAxiosError}
);

export const deleteRateLimit = createAsyncThunk(
    'rate-limiting/delete_rate_limit',
    async (apiKey: string, thunkAPI) => {
      const requestUrl = `${ apiUrl }/${ apiKey }`;
      const result = await axios.delete<any>(requestUrl);
      thunkAPI.dispatch(searchRateLimits({apiKey: ''}));
      return result;
    },
    {serializeError: serializeAxiosError}
);

// slice

export const RateLimitSlice = createSlice({
  name: 'rateLimit',
  initialState,
  reducers: {
    reset() {
      return initialState;
    },
  },
  extraReducers(builder) {
    builder
    .addCase(getRateLimit.fulfilled, (state, action) => {
      state.loading = false;
      state.rateLimit = action.payload.data;
    })
    .addCase(deleteRateLimit.fulfilled, state => {
      state.updating = false;
      state.updateSuccess = true;
      state.rateLimit = {} as IRateLimit;
    })
    .addMatcher(isFulfilled(searchRateLimits), (state, action) => {
      state.loading = false;
      state.rateLimits = action.payload.data;
      state.totalItems = parseInt(action.payload.headers['x-total-count'], 10) || 0;
    })
    .addMatcher(isFulfilled(createRateLimit, updateRateLimit), (state, action) => {
      state.updating = false;
      state.loading = false;
      state.updateSuccess = true;
      state.rateLimit = action.payload.data;
    })
    .addMatcher(isPending(searchRateLimits, getRateLimit), state => {
      state.errorMessage = null;
      state.updateSuccess = false;
      state.loading = true;
    })
    .addMatcher(isPending(createRateLimit, updateRateLimit, deleteRateLimit), state => {
      state.errorMessage = null;
      state.updateSuccess = false;
      state.updating = true;
    })
    .addMatcher(isRejected(searchRateLimits, getRateLimit, createRateLimit, updateRateLimit, deleteRateLimit), (state, action) => {
      state.loading = false;
      state.updating = false;
      state.updateSuccess = false;
      state.errorMessage = action.error.message;
    });
  },
});

export const {reset} = RateLimitSlice.actions;

// Reducer
export default RateLimitSlice.reducer;
