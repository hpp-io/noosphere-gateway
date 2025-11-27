import axios from 'axios';
import { createAsyncThunk, createSlice, isFulfilled, isPending, isRejected } from '@reduxjs/toolkit';

const initialState = {
  loading: false,
  errorMessage: null,
  usageStatisticsList: [] as any[],
  updating: false,
  updateSuccess: false,
};

const apiUrl = 'api/usage-statistics';

export const getUsageStatistics = createAsyncThunk(
  'usageStatistics/fetch_list',
  async (filters: { userId?: string; apiKey?: string; apiGroup?: string; startDate?: string; endDate?: string } = {}) => {
    const { userId, apiKey, apiGroup, startDate, endDate } = filters;
    // Create a new object with only the defined and non-empty properties
    const params = Object.fromEntries(Object.entries({ userId, apiKey, apiGroup, startDate, endDate }).filter(([_, v]) => v));
    return axios.get<any[]>(apiUrl, { params });
  }
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
      .addMatcher(isFulfilled(getUsageStatistics), (state, action) => {
        state.loading = false;
        state.usageStatisticsList = action.payload.data;
      })
      .addMatcher(isPending(getUsageStatistics), state => {
        state.errorMessage = null;
        state.updateSuccess = false;
        state.loading = true;
      })
      .addMatcher(isRejected(getUsageStatistics), (state, action) => {
        state.loading = false;
        state.updating = false;
        state.updateSuccess = false;
        state.errorMessage = action.error.message;
      });
  },
});

export const { reset } = UsageStatisticsSlice.actions;

export default UsageStatisticsSlice.reducer;
