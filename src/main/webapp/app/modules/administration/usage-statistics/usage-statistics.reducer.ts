import axios from 'axios';
import { createAsyncThunk, createSlice, isFulfilled, isPending, isRejected } from '@reduxjs/toolkit';
import { serializeAxiosError } from "app/shared/reducers/reducer.utils";

export interface IUsageStatistics {
  timestamp: Date;
  userId: string;
  apiKey: string;
  apiGroup: string;
  endpoint: string;
  method: string;
  status: number;
  duration: number;
}

const initialState = {
  loading: false,
  errorMessage: null,
  usageStatisticsList: [] as IUsageStatistics[],
  totalItems: 0,
  updating: false,
  updateSuccess: false,
};

const apiUrl = 'api/usage-statistics';

export const searchUsageStatistics = createAsyncThunk(
    'usageStatistics/fetch_list',
    async (params: {
      userId?: string;
      apiKey?: string;
      apiGroup?: string;
      startDate?: string;
      endDate?: string;
      page?: number;
      size?: number;
      sort?: string;
    }) => {
      const {page, size, sort, ...filters} = params;
      const requestUrl = `${ apiUrl }/search`;

      const cleanFilters = Object.fromEntries(Object.entries(filters).filter(([_, v]) => v != null && v !== ''));

      return axios.post<IUsageStatistics[]>(requestUrl, cleanFilters, {
        params: {page, size, sort},
      });
    },
    {serializeError: serializeAxiosError}
);

export const UsageStatisticsSlice = createSlice({
  name: 'usageStatistics',
  initialState,
  reducers: {
    reset() {
      return initialState;
    },
  },
  extraReducers(builder) {
    builder
    .addMatcher(isFulfilled(searchUsageStatistics), (state, action) => {
      state.loading = false;
      state.usageStatisticsList = action.payload.data;
      state.totalItems = parseInt(action.payload.headers['x-total-count'], 10) || 0;
    })
    .addMatcher(isPending(searchUsageStatistics), state => {
      state.errorMessage = null;
      state.updateSuccess = false;
      state.loading = true;
    })
    .addMatcher(isRejected(searchUsageStatistics), (state, action) => {
      state.loading = false;
      state.updating = false;
      state.updateSuccess = false;
      state.errorMessage = action.error.message;
    });
  },
});

export const {reset} = UsageStatisticsSlice.actions;

export default UsageStatisticsSlice.reducer;
