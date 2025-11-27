import axios from 'axios';
import { createAsyncThunk, createSlice, isFulfilled, isPending, isRejected } from '@reduxjs/toolkit';

const initialState = {
  loading: false,
  errorMessage: null,
  buckets: {},
  configs: {},
  updating: false,
  updateSuccess: false,
};

const apiUrl = '/api/admin/rate-limiting';

export const getBuckets = createAsyncThunk('rate-limiting/fetch_buckets', async () => {
  const requestUrl = `${apiUrl}/buckets`;
  return axios.get<any>(requestUrl);
});

export const getConfigs = createAsyncThunk('rate-limiting/fetch_configs', async () => {
  const requestUrl = `${apiUrl}/configs`;
  return axios.get<any>(requestUrl);
});

export const updateConfig = createAsyncThunk(
  'rate-limiting/update_config',
  async ({ key, config }: { key: string; config: any }, thunkAPI) => {
    const requestUrl = `${apiUrl}/configs/${key}`;
    return axios.post<any>(requestUrl, config);
  }
);

export const RateLimitingSlice = createSlice({
  name: 'rateLimiting',
  initialState,
  reducers: {
    reset() {
      return initialState;
    },
  },
  extraReducers(builder) {
    builder
      .addCase(getBuckets.fulfilled, (state, action) => {
        state.loading = false;
        state.buckets = action.payload.data;
      })
      .addCase(getConfigs.fulfilled, (state, action) => {
        state.loading = false;
        state.configs = action.payload.data;
      })
      .addCase(updateConfig.fulfilled, (state, action) => {
        state.loading = false;
        state.updating = false;
        state.updateSuccess = true;
      })
      .addMatcher(isPending(getBuckets, getConfigs, updateConfig), state => {
        state.errorMessage = null;
        state.updateSuccess = false;
        state.loading = true;
      })
      .addMatcher(isRejected(getBuckets, getConfigs, updateConfig), (state, action) => {
        state.loading = false;
        state.updating = false;
        state.updateSuccess = false;
        state.errorMessage = action.error.message;
      });
  },
});

export const { reset } = RateLimitingSlice.actions;

export default RateLimitingSlice.reducer;
