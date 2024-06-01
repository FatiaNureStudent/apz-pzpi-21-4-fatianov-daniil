import {All, Controller, Delete, Get, Param, Post, Put, Req, Res} from '@nestjs/common';
import { GatewayService } from './gateway.service';
import { Request, Response } from 'express';
import * as path from "node:path";

@Controller()
export class GatewayController {
    constructor(private readonly gatewayService: GatewayService) {}

    @Get('user-service/:path')
    getAuthProxy(@Req() req: Request, @Res() res: Response, @Param('path') path?: string) {
        console.log(`Proxying to USER_SERVICE_URL with path: ${path}`);
        this.gatewayService.proxy(req, res, 'USER_SERVICE_URL', path);
    }

    @Get('vehicle-station-service/:path')
    getVehicleProxy(@Req() req: Request, @Res() res: Response, @Param('path') path: string) {
        console.log(`GET request to VEHICLE_SERVICE_URL with path: ${path}`);
        this.gatewayService.proxy(req, res, 'VEHICLE_SERVICE_URL', path);
    }

    @Get('order/:path')
    getOrderProxy(@Req() req: Request, @Res() res: Response, @Param('path') path: string) {
        console.log(`GET request to ORDER_SERVICE_URL with path: ${path}`);
        this.gatewayService.proxy(req, res, 'ORDER_SERVICE_URL', path);
    }

    // @Get("/ping")
    // public ping(@Req() req: Request, @Res() res: Response) {
    //     return "Pong";
    // }

    @Post('user-service/:path')
    postAuthProxy(@Req() req: Request, @Res() res: Response, @Param('path') path?: string) {
        this.gatewayService.proxy(req, res, 'USER_SERVICE_URL', path);
    }

    @Post('vehicle-station-service/:path')
    postVehicleProxy(@Req() req: Request, @Res() res: Response, @Param('path') path: string) {
        this.gatewayService.proxy(req, res, 'VEHICLE_SERVICE_URL', path);
    }

    @Post('order/:path')
    postOrderProxy(@Req() req: Request, @Res() res: Response, @Param('path') path: string) {
        this.gatewayService.proxy(req, res, 'ORDER_SERVICE_URL', path);
    }

    // @Post("/ping")
    // public ping(@Req() req: Request, @Res() res: Response) {
    //     return "Pong";
    // }

    @Put('user-service/:path')
    putAuthProxy(@Req() req: Request, @Res() res: Response, @Param('path') path?: string) {
        this.gatewayService.proxy(req, res, 'USER_SERVICE_URL', path);
    }

    @Put('vehicle-station-service/:path')
    putVehicleProxy(@Req() req: Request, @Res() res: Response, @Param('path') path: string) {
        this.gatewayService.proxy(req, res, 'VEHICLE_SERVICE_URL', path);
    }

    @Put('order/:path')
    putOrderProxy(@Req() req: Request, @Res() res: Response, @Param('path') path: string) {
        this.gatewayService.proxy(req, res, 'ORDER_SERVICE_URL', path);
    }

    // @Put("/ping")
    // public ping(@Req() req: Request, @Res() res: Response) {
    //     return "Pong";
    // }

    @Delete('user-service/:path')
    deleteAuthProxy(@Req() req: Request, @Res() res: Response, @Param('path') path?: string) {
        this.gatewayService.proxy(req, res, 'USER_SERVICE_URL', path);
    }

    @Delete('vehicle-station-service/:path')
    deleteVehicleProxy(@Req() req: Request, @Res() res: Response, @Param('path') path: string) {
        this.gatewayService.proxy(req, res, 'VEHICLE_SERVICE_URL', path);
    }

    @Delete('order/:path')
    deleteOrderProxy(@Req() req: Request, @Res() res: Response, @Param('path') path: string) {
        this.gatewayService.proxy(req, res, 'ORDER_SERVICE_URL', path);
    }

    // @Delete("/ping")
    // public ping(@Req() req: Request, @Res() res: Response) {
    //     return "Pong";
    // }
}